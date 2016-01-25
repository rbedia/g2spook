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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;

@Parameters(commandDescription = "Upload files using SFTP")
public class CommandUpload {

    @Parameter(names = {"-d", "--directory"},
            description = "Directory to write files",
            converter = FileConverter.class,
            required = true)
    private File directory;

    @Parameter(names = {"-H"},
            description = "Host to upload to",
            required = true)
    private String hostname;

    @Parameter(names = {"-u"},
            description = "Username",
            required = true)
    private String username;

    @Parameter(names = {"-k"},
            description = "Private key file path",
            converter = FileConverter.class,
            required = true)
    private File privateKey;

    @Parameter(names = {"-p"},
            description = "Private key passphrase",
            required = true)
    private String keyPassword;

    @Parameter(names = {"-r"},
            description = "Remote directory to upload files to",
            required = true)
    private String remoteDir;

    public File getDirectory() {
        return directory;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public File getPrivateKey() {
        return privateKey;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

}
