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
package org.doxu.g2.gwc.crawler.uploader;

import com.jcraft.jsch.UserInfo;
import java.io.File;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SFTPUploader {

    private final FileSystemOptions sftpOptions;

    private final String sftpBase;

    public SFTPUploader(String host, String username, File privateKey, String keyPassphrase) throws FileSystemException {
        sftpOptions = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setIdentities(sftpOptions, new File[]{privateKey});
        SftpFileSystemConfigBuilder.getInstance().setUserInfo(sftpOptions, new SftpUserInfo(keyPassphrase));
        SftpFileSystemConfigBuilder.getInstance().setTimeout(sftpOptions, 10 * 1000);

        sftpBase = "sftp://" + username + "@" + host;
    }

    public void upload(File local, String remote) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject localFile = fsManager.toFileObject(local);
        FileObject remoteFile = fsManager.resolveFile(sftpBase + "/" + remote, sftpOptions);
        remoteFile.copyFrom(localFile, new AllFileSelector());
    }

    public static class SftpUserInfo implements UserInfo {

        private final String keyPassphrase;

        public SftpUserInfo(String keyPassphrase) {
            this.keyPassphrase = keyPassphrase;
        }

        @Override
        public String getPassphrase() {
            return keyPassphrase;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String arg0) {
            return true;
        }

        @Override
        public boolean promptPassword(String arg0) {
            return false;
        }

        @Override
        public boolean promptYesNo(String message) {
            return false;
        }

        @Override
        public void showMessage(String message) {
        }
    }
}
