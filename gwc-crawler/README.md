# G2 GWC Crawler

GWC Crawler for the G2 network. For an example of the output see: http://cache.trillinux.org/crawler/

## Building

To build the GWC crawler run
```
mvn clean install
````

This will produce a file in target/ named gwc-crawler-<version>-capsule-fat.jar which is a self contained executable jar.

## Crawling

To perform a crawl run
```
java -jar target/gwc-crawler-*-capsule-fat.jar -v crawl -d /tmp/gwc-crawler/ -s http://cache.trillinux.org/g2/bazooka.php
```

After the crawler finishes executing check the /tmp/gwc-crawler/ directory for the results.

## Uploading

To upload the files to a server using SCP you will need to do some initial setup.

Create a keypair using ssh-keygen. Enter a passphrase when prompted.
```
ssh-keygen -f gwc_rsa
```

Copy the public key to the server
```
ssh-copy-id -i gwc_rsa.pub user@host
```

Perform the upload
```
java -jar target/gwc-crawler-*-capsule-fat.jar -v upload -d /tmp/gwc-crawler/ -H host -u user -k gwc_rsa -p key_passphrase -r gwcc
```

After this finishes the files will be copied to the gwcc directory in the remote user's home directory.
