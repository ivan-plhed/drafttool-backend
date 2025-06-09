package com.ivaplahed.drafttool.service.impl;

import com.ivaplahed.drafttool.model.Champion;
import com.ivaplahed.drafttool.repository.ChampionRepository;
import com.ivaplahed.drafttool.service.ChampionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChampionServiceImpl implements ChampionService {

    @Value("${image.champion.url}")
    private String imageChampionUrl;
    @Value("${image.champion.extension}")
    private String imageChampionExtension;
    @Value("${image.splash.url}")
    private String imageSplashUrl;
    @Value("${image.splash.extension}")
    private String imageSplashExtension;

    private final ChampionRepository championRepository;

    @Override
    public Optional<Champion> getByName(String name) {
        return championRepository.findByNameIgnoreCase(name).stream().map(this::setImages).findFirst();
    }

    @Override
    public List<Champion> getAll() {
        return championRepository.findAllOrderByName().stream().map(this::setImages).toList();
    }

    @Override
    @Transactional
    public void save(Champion champion) {

        if (champion.getName().equals("NUNU &AMP; WILLUMP")) {
            champion.setName("NUNU AND WILLUMP");
        }

        Optional<Champion> optional = championRepository.findByNameIgnoreCase(champion.getName());

        if (optional.isEmpty()) {
            championRepository.save(champion);
        }
    }

    private Champion setImages(Champion champion) {

        String championName = getDataDragonName(champion.getName());

        champion.setImageChampion(
                imageChampionUrl +
                        championName +
                        imageChampionExtension
        );

        champion.setImageSplash(
                imageSplashUrl +
                        championName +
                        imageSplashExtension
        );

        return champion;
    }

    private String getDataDragonName(String displayName) {
        switch (displayName) {
            case "WUKONG": return "MonkeyKing";
            case "LEBLANC": return "Leblanc";
            case "NUNU & WILLUMP":
            case "NUNU AND WILLUMP": return "Nunu";
            case "KAI'SA":
            case "KAISA": return "Kaisa";
            case "KHA'ZIX":
            case "KHAZIX": return "Khazix";
            case "CHO'GATH":
            case "CHOGATH": return "Chogath";
            case "VEL'KOZ":
            case "VELKOZ": return "Velkoz";
            case "REK'SAI":
            case "REKSAI": return "RekSai";
            case "KOG'MAW":
            case "KOGMAW": return "KogMaw";
            case "DR. MUNDO":
            case "DR MUNDO": return "DrMundo";
            case "JARVAN IV": return "JarvanIV";
            case "MISS FORTUNE": return "MissFortune";
            case "MASTER YI": return "MasterYi";
            case "TAHM KENCH": return "TahmKench";
            case "XIN ZHAO": return "XinZhao";
            case "TWISTED FATE": return "TwistedFate";
            case "LEE SIN": return "LeeSin";
            case "AURELION SOL": return "AurelionSol";
            case "FIDDLESTICKS": return "Fiddlesticks";
            case "RENATA GLASC": return "Renata";
            case "BEL'VETH": return "Belveth";
            default:
                String[] parts = displayName.toLowerCase().split("[\\s'.]");
                StringBuilder normalized = new StringBuilder();
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        normalized.append(Character.toUpperCase(part.charAt(0)))
                                .append(part.substring(1));
                    }
                }
                return normalized.toString();
        }
    }

}
