 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.util.Scanner;

public class Tiko {
    private static final String AJURI = "org.postgresql.Driver";
    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "dbstud2.sis.uta.fi";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "";  // tahan oma kayttajatunnus
    private static final String KAYTTAJA = "";  // tahan oma kayttajatunnus
    private static final String SALASANA = "";  // tahan tietokannan salasana
    private static Connection con = null;
    private static Connection con2 = null;
    String user;
    String pw;


    public static void main(String[] args) {

        boolean stopFlag = false;
        String userName = "default";
        String userPword = "default";

        while (stopFlag == false)
        {
            Scanner reader = new Scanner(System.in);
            System.out.println("Anna opiskelijanumerosi: ");
            userName = reader.nextLine();
            System.out.println("anna salasanasi: ");
            userPword = reader.nextLine();
            stopFlag = login(Integer.parseInt(userName), userPword);
        }
        Kayttaja kayttaja = new Kayttaja(Integer.parseInt(userName), userPword);
        sessio(kayttaja);
        System.out.println("tentti oli siinä");


    }


    public static boolean login(int userName, String userPword) {
        try{
            try {
                Class.forName(AJURI);
            } catch (ClassNotFoundException poikkeus) {
                System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
            }
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
            con2 = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);

            Statement stmt = con.createStatement();


            ResultSet rs = stmt.executeQuery("select * from kayttaja where " +userName+ " = onro");
            //mikali haku palauttaa tyhjan tuloksen, lisataan  tietokantaan uusi kayttaja.
            //Kayttajan rooli alustetaan oletuksena opiskelijaksi, joka voidaan tietokannan haltijan toimesta muuttaa
            if(!rs.isBeforeFirst()){
                //Lisataan kayttaja tauluun uusi tietue
                stmt.executeUpdate("INSERT INTO kayttaja " + 
                        "VALUES ('" +userName+ "', '"+userPword+"', 'opiskelija')");
                //stmt.close();                        
                return true;
            } 
            //mikali tuloksia tulee, tarkoittaa se etta kayttaja on jo rekisteröitynyt palveluun.
            //tarkastetaan kayttajan antama salasana
            else{
                rs = stmt.executeQuery("select * from kayttaja where onro = "+userName+ " and salasana ='" + userPword+"'");
                //Tarkastetaan onko kayttaja jonka nimi === "userName" myös salasana === userPword


                //Mikali tuloksia tuli, tarkoittaa se etta salasana oli oikein
                //jos ei, kerrotaan kayttajalle ettei kirjautuminen onnistunut
                if (!rs.isBeforeFirst())
                {
                    System.out.println("Salasana virheellinen");
                    //stmt.close();
                    return false;
                }
                //mikali tuloksia tuli, luodaan uusi kayttaja jolle aletaan esittamaan testeja
                else{
                    //stmt.close();
                    return true;

                }
            }

        }
        catch(SQLException e){
            System.out.println("eka Tapahtui virhe: "+e);
        }
        return false;
    }

    public static void sessio(Kayttaja k)
    {
        String oikeaVastaus;
        ResultSet rs;
        ResultSet rs2;
        try
        {
            Kysymykset kysymykset = new Kysymykset();
            int kysymysNro = 0;
            Scanner scanner = new Scanner(System.in);
            

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt2 = con2.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            //kysytaan kysymyssetin kysymyksia, kunnes kysymykset on kayty lapi

            while (kysymykset.tulostaKysymykset(kysymysNro) != null)
            {
                System.out.println("<------------------------------------------------->");
                System.out.println("Tassa tehtavassa kasitellaan seuraavia tauluja: ");
                System.out.println("<------------------------------------------------->");
                System.out.println();
                rs = stmt.executeQuery("select * from opiskelija");
                System.out.println("OPISKELIJA");
                printResults(rs);
                System.out.println("paina enter saadaksesi seuraava taulu");
                scanner.nextLine();
                System.out.println();
                System.out.println("KURSSI");
                rs = stmt.executeQuery("select * from kurssi");
                printResults(rs);
                System.out.println("paina enter saadaksesi seuraava taulu");
                System.out.println();
                scanner.nextLine();
                rs = stmt.executeQuery("select * from suoritus");
                System.out.println("SUORITUS");
                printResults(rs);
                System.out.println("Kysymyssarjan: " + (kysymysNro+1) + ". kysymys:");
                System.out.println(kysymykset.tulostaKysymykset(kysymysNro));

                while(k.yrityksia < 3)
                {
                    try{
                        System.out.println("kirjoita vastauksesi");
                        String vastaus = scanner.nextLine();
                    

                    //tarkastetaan, etta loppuu puolipisteeseen
                    if(vastaus.charAt(vastaus.length()-1) == ';')
                    {
                        int vasenkaari = 0;
                        int oikeakaari = 0;
                        //tarkastetaan, etta oikea maara sulkuja
                        boolean sulutOk = false;
                        for (int i = 0; i<vastaus.length(); i++)
                        {
                            if (vastaus.charAt(i) == '(')
                            {
                                vasenkaari++;
                            }
                            else if (vastaus.charAt(i) == ')')
                            {
                                oikeakaari++;
                            }
                        }
                        //tarkastetaan etta sulkuja oli sama maara
                        if (vasenkaari == oikeakaari)
                        {
                            //tehdaan kysely, ja poistetaan ;
                            vastaus = vastaus.substring(0,vastaus.length()-1);
                            rs = stmt.executeQuery(vastaus);
                            //lasketaan rivimaarat
                            int firstSet = getRows(rs);
                            //kasvatetaan arvauslaskuria
                            //k.yrityksia++;
                            //Selvitetaan oikea vastaus ja poistetaan ;
                            oikeaVastaus = kysymykset.tulostaVastaukset(kysymysNro);
                            oikeaVastaus = oikeaVastaus.substring(0,oikeaVastaus.length()-1);
                            rs2 = stmt2.executeQuery(oikeaVastaus);
                             //lasketaan rivit
                            int secondSet = getRows(rs2);

                            if(compareResults(rs, rs2, firstSet, secondSet) == true)
                            {   
                                System.out.println();
                                System.out.println("<----------------OIKEIN!---------------->");
                                System.out.println();
                                System.out.println("Oikea vastaus esimerkiksi: ");
                                System.out.println(kysymykset.tulostaVastaukset(kysymysNro));
                                System.out.println();
                                printResults(rs2);
                                System.out.println();
                                System.out.println("<------------TEHTÄVÄÄN " + (kysymysNro+2) + "------------------>");
                                k.yrityksia=4;
                            }
                            else
                            {
                                System.out.println("<---------------------VÄÄRIN!------------------->");
                                k.yrityksia++;
                                if(k.yrityksia < 3) {
                                    System.out.println();
                                    System.out.println("haluttu taulu oli: ");
                                    System.out.println();
                                    printResults(rs2);
                                    System.out.println("<------------------------------------------>");
                                    System.out.println("Sinun taulusi oli tällainen: ");
                                    System.out.println();
                                    printResults(rs);
                                } else {
                                    System.out.println("Oikea vastaus esimerkiksi: ");
                                    System.out.println(kysymykset.tulostaVastaukset(kysymysNro));
                                    System.out.println();
                                    printResults(rs2);
                                    System.out.println();
                                    System.out.println("<------------TEHTÄVÄÄN " + (kysymysNro+2) + "------------------>");
                                }
                                
                            }


                        }
                        //jos ei, kerrotaan syötteen olevan virheellinen
                        else
                        {
                            k.yrityksia++;
                            if(k.yrityksia < 3) {
                                System.out.println("syöte oli virheellinen, tarkasta sulut");
                            }
                            
                            if(k.yrityksia >= 3) {
                                oikeaVastaus = kysymykset.tulostaVastaukset(kysymysNro);
                                oikeaVastaus = oikeaVastaus.substring(0,oikeaVastaus.length()-1);
                                rs2 = stmt2.executeQuery(oikeaVastaus);
                                System.out.println("Oikea vastaus esimerkiksi: ");
                                System.out.println(kysymykset.tulostaVastaukset(kysymysNro));
                                System.out.println();
                                printResults(rs2);
                                System.out.println();
                                System.out.println("<------------TEHTÄVÄÄN " + (kysymysNro+2) + "------------------>");
                            } 
                        }

                    }
                    else
                    {
                        System.out.println("haun tulee loppua puolipisteeseen");
                        k.yrityksia++;
                    }
                } catch(SQLException e){
                    System.out.println("SQL hakuvirhe ( Huom! Jos kirjoitit ääkkösiä virhe saattoi johtua siitä! ");
                    k.yrityksia++;
                    oikeaVastaus = kysymykset.tulostaVastaukset(kysymysNro);
                    oikeaVastaus = oikeaVastaus.substring(0,oikeaVastaus.length()-1);
                    rs2 = stmt2.executeQuery(oikeaVastaus);
                    if(k.yrityksia >= 3) {
                        System.out.println("Oikea vastaus esimerkiksi: ");
                        System.out.println(kysymykset.tulostaVastaukset(kysymysNro));
                        System.out.println();
                        printResults(rs2);
                        System.out.println();
                        System.out.println("<------------TEHTÄVÄÄN " + (kysymysNro+2) + "------------------>");
                    } else {
                        System.out.println();
                        System.out.println("haluttu taulu oli: ");
                        System.out.println();
                        printResults(rs2);
                        System.out.println("<------------------------------------------>");
                        System.out.println("Yritäppä uudelleen.");
                        System.out.println();
                    }
                }

                    


                }
                //nollataan yritykset, ja kysytaan haluaako kayttaja nahda oikean haun
                k.yrityksia = 0;
                    
              
                //kasvatetaan kysymyslaskuria
                kysymysNro++;


            }
        }
        catch(SQLException e){
            System.out.println("Sessiossa tapahtui virhe: "+e);
        }

    }
    public static void printResults(ResultSet rs)
    {
        try{
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String total="";
            for (int i = 1; i <= rsmd.getColumnCount(); i++) 
            {
                    String name = rsmd.getColumnName(i);
                    total = total+name + " | ";
                    System.out.print(name + " | ");
    
            }
            System.out.println("");
            for(int i = 0; i<total.length(); i++)
            {
                System.out.print('-');
            }
            System.out.println("");


            while (rs.next()) {         
                for(int i = 1 ; i <= columnsNumber; i++){
                    System.out.print(rs.getString(i) + " | "); //Print one element of a row
                }
                  System.out.println();//Move to the next line to print the next row.           
            }
           }
           catch(SQLException e){
            System.out.println("Tulostuksessa virhe: "+e);
        }
        System.out.println("");
        System.out.println("");
    }

    public static boolean compareResults(ResultSet a, ResultSet b, int c, int d)
    {
        try
        {
            ResultSetMetaData rsmda = a.getMetaData();
            ResultSetMetaData rsmda2 = b.getMetaData();
            if(c != d)
            {
                a.beforeFirst();
                b.beforeFirst();
                return false;
            }

            while (a.next() && b.next())
            {
                int i = 1;
                if(a.getString(i).equals(b.getString(i)) == false) 
                {   
                    a.beforeFirst();
                    b.beforeFirst();
                    return false;
                }
                i++;
            }
        a.beforeFirst();
        b.beforeFirst();
        return true;
        }
        catch(SQLException e){
            System.out.println("Eri tulokset: "+e);
        }
        return false;
    }

    public static int getRows(ResultSet res){
    int totalRows = 0;
    try {
        res.last();
        totalRows = res.getRow();
        res.beforeFirst();
    } 
    catch(Exception ex)  {
        return 0;
    }
    return totalRows ;    
    }
}

