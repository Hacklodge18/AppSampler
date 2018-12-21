# -*- coding: utf-8 -*-
import scrapy
from string import ascii_lowercase


class ApklinkerSpider(scrapy.Spider):
    name = 'apklinker'
    
    start_urls=[
        'https://apkpure.com/game_action'
    ]

    def parse(self, response):

        if (response.request.url.__contains__("%D") or response.request.url.__contains__("%C")
            or response.request.url.__contains__("%E") or response.request.url.__contains__("%B")):
            return

        if response.url.startswith("https://apkpure.com/game_"):

            for link in response.css("div.category-template-img a::attr(href)").extract():
                yield scrapy.Request(response.urljoin(link), callback=self.parse)
                
            more = response.css("a.loadmore::attr(href)").extract()[0]
            yield scrapy.Request(response.urljoin(more), callback=self.parse)
            
        else:
            package = response.request.url.replace('https://apkpure.com/','')
            package = package[package.index('/')+1:len(package)]
            
            request = scrapy.Request(response.request.url + '/versions', callback=self.get_apk)

            request.meta['icon']=response.css("div.icon img::attr(src)").extract()[0]

            yield request

    def get_apk(self, response):

        if response.url.endswith("Fversion"):

            package = response.request.url.replace('https://apkpure.com/','')
            package = package[package.index('/')+1:len(package)]
            package = package[:package.index('/')]
            yield {
                'name': response.css("div.title.bread-crumbs a::text").extract()[2],
                'package': package,
                'icon': response.meta['icon'],
                'url': response.css("a#download_link::attr(href)").extract()[0]
            }

        elif response.url.endswith("/versions"):
            for link in response.css("ul.ver-wrap li a::attr(href)").extract():
                if link.__contains__("APK") and not link.__contains__("XAPK"):
                    request = scrapy.Request(response.urljoin(link), callback=self.get_apk)

                    request.meta['icon']=response.meta['icon']
                    yield request
                    return
