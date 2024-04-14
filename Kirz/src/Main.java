import net.etfbl.kirz.algoritmi.IstorijskiAlgoritmi;
import net.etfbl.kirz.auth.Registracija;
import net.etfbl.kirz.datoteka.DigitalniPotpis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static boolean prihjavlen = false;
    private static boolean obavljenaOperacija = false;
    public static void main(String[] args)
    {
        System.out.println("Dobar dan i dobrososli!");
        String unos = "";
        Scanner scanner = new Scanner(System.in);
        while(!"KRAJ".equals(unos) && !obavljenaOperacija && !prihjavlen)
        {
            unos = "";
            System.out.println("Unesite neku od dostupnih komandi:");
            System.out.println("REG: Registracija");
            System.out.println("LOG: Prijava na sistem");
            System.out.println("KRAJ: Zavrsetak rada");
            if(scanner.hasNextLine())
            {
                unos = scanner.nextLine();
            }
            String unosTrim = unos.trim();
            if("REG".equals(unosTrim))
            {
                Registracija.registruj();
            }
            else if("LOG".equals(unosTrim))
            {
                prihjavlen = Registracija.prijavi();
            }
            else if ("KRAJ".equals(unosTrim))
            {
                System.out.println("Dovidjenja!");
            }
            else
            {
                System.out.println("Neispravna komanda!");
            }
        }
        String unos2 = "";
        String putanja = "users/" + Registracija.prijavljeniKorisnik + "/rezultati.txt";
        if(prihjavlen)
        {
            System.out.print("Unesite lozinku za dekrepciju i digitalni potpis fajla sa istorijom: ");
            String lozinka = scanner.nextLine();
            DigitalniPotpis.validacijaPotpisa(Registracija.prijavljeniKorisnik);

            while(!"KRAJ".equals(unos2))
            {
                DigitalniPotpis.dekripcijaFajla(Registracija.prijavljeniKorisnik, lozinka);
                boolean izvrsenaKomanda = false;
                boolean rfKomanda = false;
                System.out.println("DOSTUPNI ALGORITMI:");
                System.out.println("RF: Rail fence");
                System.out.println("MYSZ: Myszkowski");
                System.out.println("PF: Playfair");
                System.out.println("KRAJ: Zavrsetak rada");
                System.out.println("IST: Citanje istorije simulacija iz fajla");

                if(scanner.hasNextLine())
                {
                    unos2 = scanner.nextLine();
                }
                String unosTrim = unos2.trim();
                String tekstUlaz = "";
                String kljucUlaz = "";
                int kljucInt = 0;
                String algoritam = "";
                String sifrat = "";
                if("RF".equals(unosTrim))
                {
                    algoritam = "Rail fance";
                    do
                    {
                        System.out.print("Unesite tekst koji je neophodno enkriptovati (do 100 karaktera): ");
                        tekstUlaz = scanner.nextLine();
                    }while (tekstUlaz.length() >= 100);
                    System.out.print("Unesite broj kolosijeka: ");
                    kljucInt = scanner.nextInt();
                    String tekstUpper = tekstUlaz.toUpperCase();
                    String tekst = tekstUpper.replaceAll("\\s+", "");
                    sifrat = IstorijskiAlgoritmi.railFence(tekst, kljucInt);
                    izvrsenaKomanda = true;
                    rfKomanda = true;
                    scanner.nextLine();
                }
                else if("MYSZ".equals(unosTrim))
                {
                    algoritam = "Myszkowski";
                    do
                    {
                        System.out.print("Unesite tekst koji je neophodno enkriptovati (do 100 karaktera): ");
                        tekstUlaz = scanner.nextLine();
                    }while (tekstUlaz.length() >= 100);
                    System.out.print("Unesite kljuc za enkripciju: ");
                    kljucUlaz = scanner.nextLine();
                    String tekstUpper = tekstUlaz.toUpperCase();
                    String kljucUpper = kljucUlaz.toUpperCase();
                    String tekst = tekstUpper.replaceAll("\\s+", "");
                    String kljuc = kljucUpper.replaceAll("\\s+", "");
                    sifrat = IstorijskiAlgoritmi.myszkowski(tekst, kljuc);
                    izvrsenaKomanda = true;
                    //scanner.nextLine();
                }
                else if("PF".equals(unosTrim))
                {
                    algoritam = "Playfair";
                    do
                    {
                        System.out.print("Unesite tekst koji je neophodno enkriptovati (do 100 karaktera): ");
                        tekstUlaz = scanner.nextLine();
                    }while (tekstUlaz.length() >= 100);
                    System.out.print("Unesite kljuc za enkripciju: ");
                    kljucUlaz = scanner.nextLine();
                    String tekstUpper = tekstUlaz.toUpperCase();
                    String kljucUpper = kljucUlaz.toUpperCase();
                    String tekst = tekstUpper.replaceAll("\\s+", "");
                    String kljuc = kljucUpper.replaceAll("\\s+", "");
                    sifrat = IstorijskiAlgoritmi.playFair(tekst, kljuc);
                    izvrsenaKomanda = true;
                    //scanner.nextLine();
                }
                else if ("IST".equals(unosTrim))
                {
                    System.out.println("Sadrzaj:");
                    try
                    {
                        List<String> linije = Files.readAllLines(Path.of(putanja));
                        for(String linija : linije)
                        {
                            System.out.println(linija);
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("Nedostupan fajl");
                        //e.printStackTrace();
                    }
                }
                else if ("KRAJ".equals(unosTrim))
                {
                    System.out.println("Dovidjenja!");
                }
                else
                {
                    System.out.println("Neispravna komanda!");
                }
                try
                {
                    if(!izvrsenaKomanda)
                    {
                        DigitalniPotpis.enkripcijaFajla(Registracija.prijavljeniKorisnik, lozinka);
                        DigitalniPotpis.potpisivanjeDatoteke(Registracija.prijavljeniKorisnik, lozinka);
                    }
                    else
                    {
                        File rezultati = new File(putanja);
                        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(rezultati, true)));
                        if(!rfKomanda)
                        {
                            System.out.println(tekstUlaz + " | " + algoritam + " | " + kljucUlaz + " | " + sifrat);
                            pw.println(tekstUlaz + " | " + algoritam + " | " + kljucUlaz + " | " + sifrat);
                        }
                        else
                        {
                            System.out.println(tekstUlaz + " | " + algoritam + " | " + kljucInt + " | " + sifrat);
                            pw.println(tekstUlaz + " | " + algoritam + " | " + kljucInt + " | " + sifrat);
                        }
                        pw.close();
                        DigitalniPotpis.enkripcijaFajla(Registracija.prijavljeniKorisnik, lozinka);
                        DigitalniPotpis.potpisivanjeDatoteke(Registracija.prijavljeniKorisnik, lozinka);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
    }
}