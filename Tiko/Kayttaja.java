
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Kayttaja {
        String aloitusAika;
        String lopetusAika;
        int userN;
        String pw;
        int tulos;
        int yrityksia = 0;
        public Kayttaja(int a, String b)
        {
                aloitusAika = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        userN = a;
        pw = b;
        }
        public String EndTime()
        {
                String palautus = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                lopetusAika = palautus;
                return palautus;
        }
        public String getAloitusAika() {
                return aloitusAika;
        }
        public void setAloitusAika(String aloitusAika) {
                this.aloitusAika = aloitusAika;
        }
        public String getLopetusAika() {
                return lopetusAika;
        }
        public void setLopetusAika(String lopetusAika) {
                this.lopetusAika = lopetusAika;
        }
        
        
}
