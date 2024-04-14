package net.etfbl.kirz.datoteka;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DigitalniPotpis
{
    public static void potpisivanjeDatoteke(String username, String lozinka)
    {
        try
        {
            String dir = "users/" + username + "/";
            ProcessBuilder pb = new ProcessBuilder("openssl", "dgst", "-sha256", "-sign", dir + "private_key.pem", "-out", dir + "potpis.txt", dir + "rezultatiEnc.txt");
            Process process = pb.start();
            OutputStream outputStream = process.getOutputStream();
            outputStream.write((lozinka.trim() + "\n").getBytes());
            outputStream.close();
            int exitCode= process.waitFor();
            if(exitCode != 0)
            {
                System.out.println("Nepravilna lozinka - nemoguce digitalno potpisati dokument");
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void validacijaPotpisa(String username)
    {
        try
        {
            String dir = "users/" + username + "/";
            File ptps = new File(dir + "potpis.txt");
            if(ptps.exists())
            {
                ProcessBuilder pb = new ProcessBuilder("openssl", "dgst", "-sha256", "-verify", dir + "public_key.pem", "-signature", dir + "potpis.txt", dir + "rezultatiEnc.txt");
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String verifikacija = reader.readLine();
                int exitCode= process.waitFor();
                if(!"Verified OK".equals(verifikacija))
                {
                    System.out.println("<<<<Detektovana izmjena na datoteci sa sifratima!!!>>>>");
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void enkripcijaFajla(String username, String lozinka)
    {
        try
        {
            String dir = "users/" + username + "/rezultatiEnc.txt";
            String dirUlaz = "users/" + username + "/rezultati.txt";
            File rez = new File(dirUlaz);
            if(rez.exists())
            {
                ProcessBuilder pb = new ProcessBuilder("openssl", "des3", "-in", dirUlaz, "-out", dir, "-k", lozinka, "-base64");
                Process process = pb.start();
                int exitCode= process.waitFor();
                Path zaBrisanje = Paths.get(dirUlaz);
                Files.deleteIfExists(zaBrisanje);
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void dekripcijaFajla(String username, String lozinka)
    {
        try
        {
            String dirEnc = "users/" + username + "/rezultatiEnc.txt";
            String dirDec = "users/" + username + "/rezultati.txt";
            File rez = new File(dirEnc);
            if(rez.exists())
            {
                ProcessBuilder pb = new ProcessBuilder("openssl", "des3", "-d", "-in", dirEnc, "-out", dirDec, "-k", lozinka, "-base64");
                Process process = pb.start();
                int exitCode= process.waitFor();
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
