package net.questcraft;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ResourceTesting {

    @Test
    public void testRetrievingResources() throws FileNotFoundException {
        File file = new File(getClass().getClassLoader().getResource("test.sql").getFile());

        Scanner scanner = new Scanner(file);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        System.out.println(builder.toString());
    }


}
