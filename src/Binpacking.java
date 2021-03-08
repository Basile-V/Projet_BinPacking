import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Binpacking {
    File file;
    int[] data;
    int bin_size;
    int nb_item;
    int nb_bin;

    public Binpacking(String path) {
        try {
            File file = new File(path);
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            String line;
            int nb_line = 1;
            while ((line = reader.readLine()) != null) {

                if (nb_line == 1) {
                    String[] header = line.split(" ");
                    this.bin_size = Integer.parseInt(header[0]);
                    this.nb_item = Integer.parseInt(header[1]);
                    this.data = new int[this.nb_item];
                } else {
                    this.data[nb_line-2] = Integer.parseInt(line);
                }
                nb_line += 1;
            }
            System.out.println(this.nb_item);
            System.out.println(this.bin_size);
            System.out.println(this.nb_bin);
            int total_data = 0;
            for(int i=0; i < this.data.length; i++) {
                total_data += this.data[i];
                System.out.println(this.data[i]);
            }
            int min_bin = 1 + (total_data / this.bin_size);
            System.out.println("Nombre minimal de bin : " + min_bin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
