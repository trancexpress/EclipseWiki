**NO EXTRA CLEANING UP STEPS WERE TAKEN FOR `md` FILES, SEE [CLEAN UP SECTION](#Manual-clean-up-after-migration) BELOW**

### Files

- `Eclipsepedia-full.xml.zip` - archived full (minus a few pages) Eclipse wiki `xml` adjusted to fit parsing tool: https://github.com/outofcontrol/mediawiki-to-gfm
- `markdown.zip` - archived folder with the `md` pages
- `images.7z.001`, `images.7z.002`, `images.7z.003` - multi-volume archive with wiki images, separate since the wiki export doesn't include images
- `EclipseWikiCrawler.java` - Java snippet that reads a wiki exported `xml` file and downloads images that are linked in the exported pages; has problems with empty spaces in image names, download those manually (there will be printed exception stack traces when an image cannot be download)
- `DownloadPageNames.java` - Java snippet that lists all Eclipse wiki pages, to use for the wiki export; crawls over https://wiki.eclipse.org/Special:AllPages

Java snippets above contain hard-coded paths, adjust as you see fit.

### Tips for the migration process:

First, export pages via: https://wiki.eclipse.org/Special:Export

Steps to migrate by using `mediawiki-to-gfm`:

```
docker run -it --rm -v /tmp/wikiexport:/wikiexport --entrypoint /bin/bash php:8
curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer
apt-get update && apt-get install -y git vim pandoc
git clone https://github.com/outofcontrol/mediawiki-to-gfm.git
cd mediawiki-to-gfm
composer update --no-dev
./convert.php --filename=/wikiexport/Eclipsepedia-.xml --output=/wikiexport/markdown/
```

In case of big `xml` files, apply this change to `mediawiki-to-gmf`:

```
diff --git a/app/src/Convert.php b/app/src/Convert.php
index fc00640..dea6812 100644
--- a/app/src/Convert.php
+++ b/app/src/Convert.php
@@ -343,7 +343,7 @@ class Convert
      */
     public function loadData($xmlData)
     {
-        if (($xml = new \SimpleXMLElement($xmlData)) === false) {
+        if (($xml = new \SimpleXMLElement($xmlData,  LIBXML_PARSEHUGE)) === false) {
             throw new \Exception('Invalid XML File.');
         }
         $this->dataToConvert = $xml->xpath('page');
```

And call the conveter with:

`php -d memory_limit=4G ./convert.php --filename=/wikiexport/Eclipsepedia-20230228212752.xml --output=/wikiexport/markdown/`

There might be problems (usually with tables), the converter will print the last successfully converted page. So check the next one in the exported `xml` for the errors. Example problems are, no closing `|}` symbol for a table, or a `|` symbol in a code block needs to be escaped.

The full `xml` file in `Eclipsepedia-full.xml.zip` contains pages with fixed formatting, though in some cases the fixes might have damaged the look of the page.

### Manual clean up after migration

Formatting might be messed up, best to check each page and adjust formatting where necessary. Color tags don't seem to be respected by `GitHub` wikis.

For `GitHub` links to work, wiki links (`grep` for `wikilink`) need to be adjusted manually. The link is, URL of the wiki followed by the page file name (spaces are replaced with `-`) without the `md` extension.

Image links must likewise be adjusted manually.

For examples check `md` files from:

- https://github.com/eclipse-platform/eclipse.platform.swt.wiki.git
- https://github.com/eclipse-jdt/eclipse.jdt.core.wiki.git