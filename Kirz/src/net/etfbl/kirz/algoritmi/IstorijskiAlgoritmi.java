package net.etfbl.kirz.algoritmi;

import java.util.*;

public class IstorijskiAlgoritmi
{
    public static String railFence(String ulaz, int brojKolosjeka)
    {
        Character[][] pruga = new Character[ulaz.length()][brojKolosjeka];
        char[] ulazChars = ulaz.toCharArray();
        boolean silaznaPutanja = true;
        int j = 0;
        for(int i = 0; i < ulaz.length(); i++)
        {
            if(silaznaPutanja && i != 0)
            {
                j++;
            }
            else if(!silaznaPutanja)
            {
                j--;
            }
            pruga[i][j] = ulazChars[i];
            if(j == 0 && i != 0)
            {
                silaznaPutanja = true;
            }
            else if (j == brojKolosjeka - 1)
            {
                silaznaPutanja = false;
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int k = 0; k < pruga[0].length; k++)
        {
            for(int i = 0; i < pruga.length; i++)
            {
                if(pruga[i][k] != null)
                {
                    sb.append(pruga[i][k]);
                }
            }
        }
        return sb.toString();
    }

    public static String myszkowski(String ulaz, String kljuc)
    {
        int brojRedova = ulaz.length() / kljuc.length() + 1;
        Character[][] tabela = new Character[kljuc.length()][brojRedova];
        char[] ulazChars = ulaz.toCharArray();
        char[] kljucChars = kljuc.toCharArray();
        int i = 0;
        for (int k = 0; k < brojRedova; k++)
        {
            //System.out.println();
            for (int j = 0; j < kljuc.length(); j++)
            {
                tabela[j][k] = ulazChars[i];
                //System.out.print(tabela[j][k]);
                i++;
                if(i == ulazChars.length)
                {
                    break;
                }
            }
        }
        HashMap<Character, Integer> mapa = new HashMap<>();
        for(int j = 0; j < kljuc.length(); j++)
        {
            if(mapa.containsKey(kljucChars[j]))
            {
                int val = mapa.get(kljucChars[j]);
                mapa.remove(kljucChars[j]);
                mapa.put(kljucChars[j], val + 1);
            }
            else
            {
                mapa.put(kljucChars[j], 1);
            }
        }
        //System.out.println();
        Set<Character> set = mapa.keySet();
        List<Character> sortiranaSlova = new ArrayList<>(set);
        Collections.sort(sortiranaSlova);
        StringBuilder sb = new StringBuilder();
        for(Character c : sortiranaSlova)
        {
            //System.out.println(c + ": " + mapa.get(c));
            List<Integer> indexes = new ArrayList<>();
            int index = kljuc.indexOf(c);
            while(index != -1)
            {
                indexes.add(index);
                index = kljuc.indexOf(c, index + 1);
            }
            for(int j = 0; j < brojRedova; j++)
            {
                for(int ind : indexes)
                {
                    if(tabela[ind][j] != null)
                    {
                        sb.append(tabela[ind][j]);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String playFair(String ulaz, String kljuc)
    {
        Character[][] tabela = new Character[5][5];
        char[] slova = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        char[] ulazChars = ulaz.toCharArray();
        char[] kljucChars = kljuc.toCharArray();
        char[] ulaznaTabela = new char[25];

        int brojac = 0;
        for(int i = 0; i < kljucChars.length; i++)
        {
            if(!sadrziSlovo(ulaznaTabela, kljucChars[i]) && kljucChars[i] != 'J')
            {
                ulaznaTabela[brojac] = kljucChars[i];
                brojac++;
            }
        }

        for(int i = 0; i < slova.length; i++)
        {
            if(!sadrziSlovo(ulaznaTabela, slova[i]))
            {
                ulaznaTabela[brojac] = slova[i];
                brojac++;
            }
        }

        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                tabela[i][j] = ulaznaTabela[i*5 + j];
            }
        }

        /*for(int i = 0; i < 5; i++)
        {
            System.out.println();
            for(int j = 0; j < 5; j++)
            {
                System.out.print(tabela[i][j]);
            }
        }
        System.out.println();*/

        List<Character> normalizovanUlaz = new ArrayList<>();
        for(int i = 0; i < ulazChars.length; i++)
        {
            if(ulazChars[i] != 'J')
            {
                normalizovanUlaz.add(ulazChars[i]);
            }
            else
            {
                normalizovanUlaz.add('I');
            }
            if((i + 1) != ulazChars.length)
            {
                if(ulazChars[i] == ulazChars[i+1])
                {
                    normalizovanUlaz.add('X');
                }
                else if (ulazChars[i] == 'J' && ulazChars[i + 1] == 'I' || ulazChars[i] == 'I' && ulazChars[i + 1] == 'J')
                {
                    normalizovanUlaz.add('X');
                }
            }
        }
        if(normalizovanUlaz.size() % 2 == 1)
        {
            normalizovanUlaz.add('X');
        }
        StringBuilder sb = new StringBuilder();
        for(Character c : normalizovanUlaz)
        {
            sb.append(c);
        }

        String tekst = sb.toString();
        char[] tekstChars = tekst.toCharArray();
        List<String> parovi = new ArrayList<>();
        for(int i = 0; i < tekstChars.length; i+=2)
        {
            String temp = "";
            temp += tekstChars[i];
            temp += tekstChars[i + 1];
            parovi.add(temp);
        }

        StringBuilder izlaz = new StringBuilder();
        for(String p : parovi)
        {
            char[] pChars = p.toCharArray();
            int i1 = 0, j1 = 0, i2 = 0, j2 = 0;
            for(int i = 0; i < 5; i++)
            {
                for(int j = 0; j < 5; j++)
                {
                    if(tabela[i][j] == pChars[0])
                    {
                        i1 = i;
                        j1 = j;
                    }
                    else if(tabela[i][j] == pChars[1])
                    {
                        i2 = i;
                        j2 = j;
                    }
                }
            }

            if(i1 != i2 && j1 != j2)
            {
                izlaz.append(tabela[i1][j2]);
                izlaz.append(tabela[i2][j1]);
            }
            else if(i1 == i2)
            {
                if(j1 + 1 < 5)
                {
                    izlaz.append(tabela[i1][j1 + 1]);
                }
                else
                {
                    izlaz.append(tabela[i1][0]);
                }
                if(j2 + 1 < 5)
                {
                    izlaz.append(tabela[i2][j2 + 1]);
                }
                else
                {
                    izlaz.append(tabela[i1][0]);
                }
            }
            else if(j1 == j2)
            {
                if(i1 + 1 < 5)
                {
                    izlaz.append(tabela[i1 + 1][j1]);
                }
                else
                {
                    izlaz.append(tabela[0][j1]);
                }
                if(i2 + 1 < 5)
                {
                    izlaz.append(tabela[i2 + 1][j2]);
                }
                else
                {
                    izlaz.append(tabela[0][j2]);
                }
            }
        }
        return izlaz.toString();
    }

    private static boolean sadrziSlovo(char[] text, char slovo)
    {
        for(int i = 0; i < text.length; i++)
        {
            if(slovo == text[i])
            {
                return true;
            }
        }
        return false;
    }

}
