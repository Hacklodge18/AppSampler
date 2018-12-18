# -*- coding: utf-8 -*-
import scrapy
from string import ascii_lowercase


class GamefinderSpider(scrapy.Spider):
    name = 'gamefinder'
   
    
    def start_requests(self):
        urls = ['https://play.google.com/store/apps/details?id=com.supercell.brawlstars', 'https://play.google.com/store/apps/details?id=klb.android.lovelive_en', 'https://play.google.com/store/apps/details?id=com.noodlecake.altosodyssey', 'https://play.google.com/store/apps/details?id=com.kongregate.mobile.tinkerisland.google', 'https://play.google.com/store/apps/details?id=com.jagex.oldscape.android', 'https://play.google.com/store/apps/details?id=com.namcobandaigames.pacmantournaments', 'https://play.google.com/store/apps/details?id=com.hasbro.riskbigscreen', 'https://play.google.com/store/apps/details?id=com.CloudMacaca.Flight', 'https://play.google.com/store/apps/details?id=com.mousecity.faraway3', 'https://play.google.com/store/apps/details?id=com.ubisoft.accovenant', 'https://play.google.com/store/apps/details?id=com.MikaMobile.Battleheart', 'https://play.google.com/store/apps/details?id=com.empire.grow.rome', 'https://play.google.com/store/apps/details?id=com.minimo.miniracingadventures', 'https://play.google.com/store/apps/details?id=com.huuugetaptapgames.hillracer']

        for c in ascii_lowercase:
            link = "https://play.google.com/store/search?c=apps&price=1&q=" + c
            yield scrapy.Request(url=link, callback=self.parse)

        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):

        if response.url.startswith("https://play.google.com/store/search?c=apps&price=1"):
            
            for link in response.css("a.card-click-target::attr(href)").extract():
                yield scrapy.Request(response.urljoin(link), callback=self.parse)
            return
        
        genre = response.css("a.hrTbp.R8zArc::text")[1].extract()
        accepted_genres = {'Action', 'Adventure', 'Arcade', 'Board', 'Card', 'Casual', 'Music', 'Puzzle', 'Racing', 'Role Playing', 'Simulation', 'Strategy', 'Trivia', 'Word'}
        if genre not in accepted_genres:
            return

        for link in response.css("a.poRVub::attr(href)").extract():
            yield scrapy.Request(response.urljoin(link), callback=self.parse)

        installButton = response.css("button.LkLjZd.ScJHi.HPiPcc.IfEcue::text").extract()[0];
        if installButton != "Install":
            return
        

        package = response.request.url.split("=")[1]
        accepted_packages = {"com", "net", "games", "org", "indie", "klb"}
        if package.split(".")[0] not in accepted_packages:
            return
        
        yield {
            'name': response.css("h1.AHFaub span::text").extract()[0],
            'package': package,
            'icon': response.css("div.dQrBL img::attr(src)").extract()[0],
        }
