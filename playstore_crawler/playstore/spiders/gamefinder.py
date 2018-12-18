# -*- coding: utf-8 -*-
import scrapy


class GamefinderSpider(scrapy.Spider):
    name = 'gamefinder'
   
    start_urls = ['https://play.google.com/store/apps/details?id=com.supercell.brawlstars']

    def parse(self, response):
        
        genre = response.css("a.hrTbp.R8zArc::text")[1].extract()
        accepted_genres = {'Action', 'Adventure', 'Arcade', 'Board', 'Card', 'Casual', 'Music', 'Puzzle', 'Racing', 'Role Playing', 'Simulation', 'Sports', 'Strategy', 'Trivia', 'Word'}
        if genre not in accepted_genres:
            return

        package = response.request.url.split("=")[1]
        if package.split(".")[0] != "com":
            return
        
        yield {
            'name': response.css("h1.AHFaub span::text").extract()[0],
            'package': package,
            'icon': response.css("div.dQrBL img::attr(src)").extract()[0],
        }

        for link in response.css("a.poRVub::attr(href)").extract():
            yield scrapy.Request(response.urljoin(link), callback=self.parse)
