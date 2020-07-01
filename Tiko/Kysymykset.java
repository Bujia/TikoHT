
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Kysymykset {
        //tarttis sit kaivaa niita kysymyksia jostain?
    String kysymys1 = "Valitse kaikki opettajien nimet";
    String kysymys2 = "Valitse opiskelijoiden nimet, joiden paaaine "
            + "on Matematiikka";
    String kysymys3 = "Valitse ne opiskelijat, "
            + "joilla ei ole yhtakaan suoritusta";
    String kysymys4 = "Lisaa opiskelijoilla 'Matti Mallikas' suoritus"
            + " kurssista 'Kissakuvien historia' arvosanalla 5. "
            + "Huom! Muista lisata myos jarjestyksessa seuraava krno ja pvm";
    String kysymys5 = "Valitse opiskelijoiden lukumaara sarakkeeseen 'lkm' "
            + ", jotka ovat suorittaneet kurssin 'Lineaarialgebra'";
    
    String vastaus1 = "SELECT DISTINCT opettajan_nimi FROM kurssi;";
    String vastaus2 = "SELECT DISTINCT onimi FROM "
            + "opiskelija WHERE paaaine = 'Matematiikka';";
    String vastaus3 = "SELECT onimi FROM opiskelija WHERE NOT EXISTS "
            + "(SELECT onimi FROM suoritus "
            + "WHERE suoritus.opiskelijan_nimi = opiskelija.onimi);";
    String vastaus4 = "INSERT INTO suoritus VALUES "
            + "(6, 'Kissakuvien historiaa', 'Matti Mallikas', 5, '2017-03-20');";
    String vastaus5 = "SELECT count(opiskelijan_nimi) "
            + "AS lkm FROM suoritus WHERE kurssinNimi = 'Lineaarialgebra';";
    
    List<String> kysymyslista = Arrays.asList(kysymys1, kysymys2,
            kysymys3, kysymys4, kysymys5);
    List<String> vastauslista = Arrays.asList(vastaus1, vastaus2,
            vastaus3, vastaus4, vastaus5);


    public String tulostaKysymykset(int kysymysNro){
    	if(kysymysNro <= kysymyslista.size()){
        	return kysymyslista.get(kysymysNro);
    	} else {
    		return null;
    	}
    }
    
    public String tulostaVastaukset(int vastausNro){
        return vastauslista.get(vastausNro);
    }
}
