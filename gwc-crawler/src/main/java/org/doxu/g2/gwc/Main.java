/**
 * Copyright 2016 Rafael Bedia
 *
 * This file is part of g2spook.
 *
 * g2spook is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * g2spook is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * g2spook. If not, see <http://www.gnu.org/licenses/>.
 */
package org.doxu.g2.gwc;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileSystemException;
import org.doxu.g2.gwc.crawler.AppInfo;
import org.doxu.g2.gwc.crawler.Crawler;
import org.doxu.g2.gwc.crawler.CrawlerFile;
import org.doxu.g2.gwc.crawler.OutputFiles;
import org.doxu.g2.gwc.crawler.uploader.SFTPUploader;

public class Main {

    public static void main(String[] args) {
        CommandMain commandMain = new CommandMain();
        JCommander jc = new JCommander(commandMain);

        CommandCrawl commandCrawl = new CommandCrawl();
        jc.addCommand("crawl", commandCrawl);
        CommandUpload commandUpload = new CommandUpload();
        jc.addCommand("upload", commandUpload);

        try {
            jc.parse(args);

            if (commandMain.printVersion()) {
                System.out.println("gwc-crawler " + AppInfo.VERSION);
                System.exit(0);
            }

            String commandName = jc.getParsedCommand();
            if (commandName == null) {
                System.out.println("No command specified.");
                System.exit(1);
            }
            switch (commandName) {
                case "crawl":
                    crawl(commandMain, commandCrawl);
                    break;
                case "upload":
                    upload(commandMain, commandUpload);
                    break;
                default:
                    System.out.println("Unsupported command: " + commandName);
                    break;
            }
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            jc.usage();
            System.exit(1);
        }
    }

    private static void crawl(CommandMain commandMain, CommandCrawl crawl) {
        String startUrl = crawl.getGwcSeed();
        File outputDir = crawl.getDirectory();
        Crawler crawler = new Crawler(startUrl);
        crawler.crawl();
        crawler.writeOutput(outputDir);
        if (commandMain.isVerbose()) {
            crawler.printStats();
        }
    }

    private static void upload(CommandMain commandMain, CommandUpload commandUpload) {
        try {
            String host = commandUpload.getHostname();
            String username = commandUpload.getUsername();
            File privateKey = commandUpload.getPrivateKey();
            String keyPassword = commandUpload.getKeyPassword();
            SFTPUploader uploader = new SFTPUploader(host, username, privateKey, keyPassword);
            File localDir = commandUpload.getDirectory();
            String remoteDir = commandUpload.getRemoteDir();

            for (CrawlerFile file : OutputFiles.list()) {
                uploader.upload(new File(localDir, file.local), remoteDir + "/" + file.remote);
            }
        } catch (FileSystemException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
