# Monix crawler to crawl HappyFresh for product data and images.

This crawler uses Monix instead of Akka. Needed to recreate a new project due some inexplicable SBT error 

Dir structure
```
- root
 - log *make this*
 - happy *make this*
   - store1
   - store2
   - (all these store dirs are created by HappyStoresCrawl )
   - store
     - products.txt (created by HappyItemCrawl)
     - info.txt (created by HappyStoresCrawl)
     - products (created by HappyImageCrawl)
       - item1
         - itemimg1
         - itemimg2
       - item2
       - store 
 - source
 - ...
```

HappyStoresCrawl
- gets all store infos in an area
- input: coordinates of areas

HappyItemCrawl
- gets product info of store
- needs to have the store dirs and info.txt ready from HappyStoresCrawl
- supports rewrite and missing_only (only fetches products not yet saved) modes

HappyImageCrawl
- gets products or store images
- store images need info.txt
- product images need products.txt
- needs to have everything from HappyStoresCrawl and products.info from HappyItemCraw



### Honest Bee crawler has been deprecated
js
Builds insert query from store info on Google Maps.

- find store page on Google Maps, copy its text to the file
- one or two differs in where the text is copied from

akka
Crawls json and images


- needs department and category list
- first copy outer html from a store page

![Honestbee Store](honestbee01.jpg)

- then extract the hrefs using this tool http://tools.buzzstream.com/link-building-extract-urls

- paste the result to CategoryParser, run, and paste the result to Seed

- run StartImageCrawler or StartImageCrawler

Next time use Google Maps API (non free)