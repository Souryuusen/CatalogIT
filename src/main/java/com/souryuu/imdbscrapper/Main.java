package com.souryuu.imdbscrapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.souryuu.imdbscrapper.entity.MovieData;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    private static SessionFactory factory;
    public static void main(String[] args) throws IOException, InterruptedException {
//        StringBuilder sb = new StringBuilder();
//
//        try {
//            URL url = new URL("https://www.imdb.com/title/tt2911666/");
//
//            URLConnection connection = url.openConnection();
//            connection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
//            connection.connect();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line = "";
//            while((line=br.readLine()) != null) {
//                sb.append(line);
//                sb.append("\n");
//            }
//
//            String s1 = sb.toString();
//
//            String directors = s1.substring(s1.indexOf("Directors"), s1.indexOf("Writer"));
//
//            List<String> directorList = new ArrayList<>();
//
//            while(directors.contains("tt_ov_dr")) {
//                directors = directors.substring(directors.indexOf("tt_ov_dr")+10);
//                System.out.println(directors.substring(0, directors.indexOf("</a></li>")));
//                directorList.add(directors.substring(0, directors.indexOf("</a></li>")));
//            }
//
//            IMDBScrapper scrapper = new IMDBScrapper("https://www.imdb.com/title/tt3915174/");
//            scrapper.printContent();
//
//            Elements elements = scrapper.getHtmlContent().selectXpath("//*[@id=\"keywords_content\"]/table/tbody/tr/td[1]/div[1]/a | //*[@id=\"keywords_content\"]/table/tbody/tr/td[2]/div[1]/a");
//            for(Element e : elements) {
//                System.out.println(e.ownText());
//            }
//            System.out.println(elements.size());
//            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\Users\\grzeg\\OneDrive\\Dokumenty\\test.txt")));
//            bw.write(scrapper.getHtmlContent().toString());
//            bw.flush();
//            bw.close();
//            Class.forName ("org.h2.Driver");
//            Connection conn = DriverManager.getConnection ("jdbc:h2:C:/Users/grzeg/OneDrive/Dokumenty/testdb", "sa","");
//            Statement stmt = conn.createStatement();
//
//            ResultSet rs = stmt.executeQuery("select * from tv_series;");
//            while(rs.next()) {
//                System.out.println(rs.getInt(1) + ":" + rs.getString(2));
//            }

//            br.close();
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        if(sb != null && sb.length() > 0) {
//            //System.out.println(sb.toString());
//        }

//        hibernateTest();
//
//        TVSerie serie = new TVSerie();
//        serie.setRating(3);
//        serie.setReview("bla bla hibernate");
//        serie.setImdbLink("www.google.pl");
//        serie.setTitle("Test Hibernate 2");
//        serie.setSeasonCount(7);
//
//        //Get Session
//        Session session = factory.getCurrentSession();
//        //start transaction
//        session.beginTransaction();
//        //Save the Model object
//        session.persist(serie);
//        //Commit transaction
//        session.getTransaction().commit();
//        System.out.println("Serie ID="+serie.getSerieID());
//
//        session = factory.getCurrentSession();
//        session.beginTransaction();
//        Query query = session.createQuery("from TVSerie");
//        List<TVSerie> list = query.list();
//
//        for(TVSerie s : list) {
//            System.out.println(s.getSerieID() + ":\t" + s.getTitle());
//        }
//
//
//        //terminate session factory, otherwise program won't end
//        factory.close();
        File input = new File("C:\\Users\\grzeg\\OneDrive\\Dokumenty\\scrapper_test\\" + "test_set" + ".txt");
//        Scanner sc = new Scanner(input);
//        while(sc.hasNext()) {
//            String url = sc.nextLine();
//            MovieDataExtractor mde = new MovieDataExtractor(url);
////            MovieDataExtractor mde = new MovieDataExtractor("https://www.imdb.com/title/tt3915174/");
//            mde.extract();
//
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            mapper.enable(SerializationFeature.INDENT_OUTPUT);
//            MovieData data = mde.getRetrievedData();
//            mapper.writeValue(new File("C:\\Users\\grzeg\\OneDrive\\Dokumenty\\scrapper_test\\" + data.getTitle().replaceAll("\\W+", "") + ".json"), data);
//            String path = "C:\\image.jpg";
//            URL coverUrl = new URL(data.getCoverURL());
//            BufferedImage image = ImageIO.read(coverUrl);
//            ImageIO.write(image, "jpg", new File("C:\\Users\\grzeg\\OneDrive\\Dokumenty\\scrapper_test\\" + data.getTitle().replaceAll("\\W+", "") + ".jpeg"));
//        }

        IMDBScrapper scrapper = new IMDBScrapper(input);
        scrapper.multithreadedScrape(5);
        while(!scrapper.isProcessingDone()) Thread.sleep(500);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        for(MovieData d : scrapper.getScrapedData()) {
            String filepath = "C:\\Users\\grzeg\\OneDrive\\Dokumenty\\scrapper_test\\" + d.getTitle().replaceAll("\\W+", "");
            mapper.writeValue(new File(filepath + ".json"), d);
            String path = "C:\\image.jpg";
            URL coverUrl = new URL(d.getCoverURL());
            BufferedImage image = ImageIO.read(coverUrl);
            ImageIO.write(image, "jpg", new File(filepath + ".jpeg"));
        }
    }

    private static void hibernateTest() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            factory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

}
