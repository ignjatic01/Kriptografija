package net.etfbl.kirz.auth;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.List;
import java.util.Scanner;

public class Registracija
{

    public static String prijavljeniKorisnik;
    public static void registruj()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("REGISTRACIJA");
        String username = "";
        do
        {
            System.out.print("Korisnicko ime: ");
            username = scanner.nextLine();
        } while (provjeriUsername(username));
        System.out.print("email: ");
        String email = scanner.nextLine();
        System.out.print("Lozinka: ");
        String lozinka = scanner.nextLine();

        String keyDirectoryPath = "users/" + username;
        File keyDir = new File(keyDirectoryPath);
        if(!keyDir.exists())
        {
            keyDir.mkdir();
        }

        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            Path privateKeyFile = Paths.get(keyDirectoryPath + File.separator + "private_key.der");
            Path privatePemKeyFile = Paths.get(keyDirectoryPath + File.separator + "private_key.pem");
            Path publicPemKeyFile = Paths.get(keyDirectoryPath + File.separator + "public_key.pem");
            byte[] privateKeyBytes = privateKey.getEncoded();
            Files.write(privateKeyFile, privateKeyBytes);
            System.out.print("Unesite lozinku za zastitu kljuca: ");
            String pemLozinka = scanner.nextLine();

            ProcessBuilder pb = new ProcessBuilder("openssl", "rsa", "-in", privateKeyFile.toString(),
                    "-out", privatePemKeyFile.toString(), "-des3", "-passout", "pass:" + pemLozinka.trim());
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0)
            {
                System.out.println("Privatni ključ uspješno zaštićen DES3 enkripcijom: " + privatePemKeyFile.toAbsolutePath());
            }
            else
            {
                System.err.println("Greška prilikom zaštite privatnog ključa.");
            }
            Files.deleteIfExists(privateKeyFile);

            ProcessBuilder pbJK = new ProcessBuilder("openssl", "rsa", "-in", privatePemKeyFile.toString(), "-inform", "PEM", "-pubout", "-out", publicPemKeyFile.toString());
            Process processJK = pbJK.start();
            OutputStream outputStreamJK = processJK.getOutputStream();
            outputStreamJK.write((pemLozinka.trim() + "\n").getBytes());
            outputStreamJK.close();
            int exitCodeJK = processJK.waitFor();

            //generisanje zahtjeva za sertifikat
            ProcessBuilder pbSertifikat = new ProcessBuilder("openssl", "req", "-new", "-key", keyDirectoryPath + File.separator + "private_key.pem",
                    "-config", "users/ca/openssl.cnf", "-out", "users/ca/requests/" + username + "Req.csr");
            Process processSertifikat = pbSertifikat.start();
            OutputStream outputStream = processSertifikat.getOutputStream();
            outputStream.write((pemLozinka.trim() + "\n").getBytes());
            for(int i = 0; i < 5; i++)
            {
                outputStream.write("\n".getBytes());
            }
            outputStream.write((username + "\n").getBytes());
            outputStream.write((email + "\n").getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();

            int exitCodeSertifikat = processSertifikat.waitFor();

            //potpisivanje sertifikata
            ProcessBuilder pbPotpis = new ProcessBuilder("openssl", "ca", "-in", "users/ca/requests/" + username + "Req.csr",
                     "-keyfile", "users/ca/private/private4096.key", "-cert", "users/ca/rootca.pem", "-out",
                    "users/ca/certs/" + username + "Cert.crt", "-config", "users/ca/openssl.cnf");
            Process processPotpis = pbPotpis.start();
            OutputStream outputStreamPotpis = processPotpis.getOutputStream();
            outputStreamPotpis.write(("sigurnost" + "\n").getBytes());
            outputStreamPotpis.write(("y" + "\n").getBytes());
            outputStreamPotpis.write(("y" + "\n").getBytes());
            outputStreamPotpis.close();
            int exitCodePotpis = processPotpis.waitFor();
            if (exitCodePotpis == 0)
            {
                System.out.println("Sertifikat je uspjesno kreiran: " + "users/ca/certs/" + username + "Cert.crt");
            }
            else
            {
                System.err.println("Greška prilikom izdavanja sertifikata.");
            }
            Path sourceSertifikat = Paths.get("users/ca/certs/" + username + "Cert.crt");
            Path destSertifikat = Paths.get("users/" + username + "/Cert.crt");
            Files.copy(sourceSertifikat, destSertifikat); //Korisnicka kopija sertifikata
            File korisnici = new File("users/korisnici.txt");
            ProcessBuilder pbLozinka = new ProcessBuilder("openssl", "passwd", "-5", lozinka);
            Process processLozinka = pbLozinka.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processLozinka.getInputStream()));
            String passHash = reader.readLine();
            int exitCodeLozinka = processLozinka.waitFor();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(korisnici, true)));
            pw.println(username + "#" + passHash);
            pw.close();
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean provjeriUsername(String username)
    {
        try
        {
            File korisnici = new File("users/korisnici.txt");
            if(korisnici.exists())
            {
                List<String> sviKorisnici = Files.readAllLines(korisnici.toPath());
                for(String korisnik : sviKorisnici)
                {
                    if(username.equals(korisnik.split("#")[0]))
                    {
                        System.out.println("Korisnicko ime je vec upotrebljeno!");
                        return true;
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean prijavi()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("PRIJAVA");
        String sertifikat = "";
        do
        {
            System.out.print("Unesite putanju do sertifikata: ");
            sertifikat = scanner.nextLine();
        }while(!sertifikat.endsWith(".crt"));
        ProcessBuilder pb = new ProcessBuilder("openssl", "verify", "-CAfile", "users/ca/rootca.pem", sertifikat);
        try
        {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String verifikacija = reader.readLine();
            int exitCode= process.waitFor();
            if("OK".equals(verifikacija.split(" ")[1]))
            {
                System.out.println("Sertifikat prepoznat od strane CA tijela");
            }
            else
            {
                System.out.println("Sertifikat nije prepoznat od strane CA tijela");
                return false;
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.print("Unesite korisnicko ime: ");
        String username = scanner.nextLine();
        System.out.print("Unesite lozinku: ");
        String lozinka = scanner.nextLine();
        try
        {
            File korisnici = new File("users/korisnici.txt");
            if(korisnici.exists())
            {
                List<String> sviKorisnici = Files.readAllLines(korisnici.toPath());
                for(String korisnik : sviKorisnici)
                {
                    if(username.equals(korisnik.split("#")[0]))
                    {
                        String otisakLozinke = korisnik.split("#")[1];
                        String salt = otisakLozinke.split("\\$")[2];
                        ProcessBuilder pbLozinka = new ProcessBuilder("openssl", "passwd", "-5", "-salt", salt, lozinka);
                        Process processLozinka = pbLozinka.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(processLozinka.getInputStream()));
                        String passHash = reader.readLine();
                        reader.close();
                        if(otisakLozinke.equals(passHash))
                        {
                            System.out.println("Dobrodosao/la " + username + "! [USPJESNA PRIJAVA]");
                            prijavljeniKorisnik = username;
                            return true;
                        }
                        else
                        {
                            System.out.println("Lozinka nije tacno unesena");
                            return false;
                        }
                    }
                }
            }
            System.out.println("Korisnik ne postoji u sistemu!");
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
