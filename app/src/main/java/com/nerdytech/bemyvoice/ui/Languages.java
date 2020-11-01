package com.nerdytech.bemyvoice.ui;

import java.util.HashMap;

public class Languages {
    static HashMap<String,String> languages;
    static HashMap<String,String> voice_lang;
    static HashMap<String,String[]> voice;

    static {
        //languages
        languages=new HashMap<>();
        languages.put("Afrikaans","af");
        languages.put("Arabic","ar");
        languages.put("Belarusian","be");
        languages.put("Bulgarian","bg");
        languages.put("Bengali","bn");
        languages.put("Catalan","ca");
        languages.put("Czech","cz");
        languages.put("Welsh","cy");
        languages.put("Danish","da");
        languages.put("German","de");
        languages.put("Greek","el");
        languages.put("English","en");
        languages.put("Esperanto","eo");
        languages.put("Spanish","es");
        languages.put("Estonian","et");
        languages.put("Persian","fa");
        languages.put("Finnish","fi");
        languages.put("French","fr");
        languages.put("Irish","ga");
        languages.put("Galician","gl");
        languages.put("Gujarati","gu");
        languages.put("Hebrew","he");
        languages.put("Hindi","hi");
        languages.put("Croatian","hr");
        languages.put("Haitian","ht");
        languages.put("Hungarian","hu");
        languages.put("Indonesian","id");
        languages.put("Italian","it");
        languages.put("Japanese","ja");
        languages.put("Georgian","ka");
        languages.put("Kannada","kn");
        languages.put("Korean","ko");
        languages.put("Lithuanian","lt");
        languages.put("Latvian","lv");
        languages.put("Macedonian","mk");
        languages.put("Marathi","mr");
        languages.put("Malay","ms");
        languages.put("Maltese","mt");
        languages.put("Dutch","nl");
        languages.put("Norwegian","no");
        languages.put("Polish","pl");
        languages.put("Portuguese","pt");
        languages.put("Romanian","ro");
        languages.put("Russian","ru");
        languages.put("Slovak","sk");
        languages.put("Albanian","sq");
        languages.put("Swedish","sv");
        languages.put("Swahili","sw");
        languages.put("Tamil","ta");
        languages.put("Telugu","te");
        languages.put("Thai","th");
        languages.put("Tagalog","tl");
        languages.put("Turkish","tr");
        languages.put("Ukrainian","uk");
        languages.put("Urdu","ur");
        languages.put("Vietnamese","vi");
        languages.put("Chinese","zh");

        //voice_lang and voice
        voice=new HashMap<>();
        voice_lang=new HashMap<>();
        //Arabic
        voice_lang.put("Arabic","ar-XA");
        voice.put("ar-XA",new String[]{"ar-XA-Standard-A","ar-XA-Standard-B","ar-XA-Standard-C","ar-XA-Standard-D","ar-XA-Wavenet-A","ar-XA-Wavenet-B","ar-XA-Wavenet-C"});

        //Bengali (India)
        voice_lang.put("Bengali (India)","bn-IN");
        voice.put("bn-IN",new String[]{"bn-IN-Standard-A","bn-IN-Standard-B"});

          //Chinese (Hong Kong)
        voice_lang.put("Chinese (Hong Kong)","yue-HK");
        voice.put("yue-HK",new String[]{"yue-HK-Standard-A","yue-HK-Standard-B","yue-HK-Standard-C","yue-HK-Standard-D"});

          //Czech (Czech Republic)
        voice_lang.put("Czech (Czech Republic)","cs-CZ");
        voice.put("cs-CZ",new String[]{"cs-CZ-Standard-A","cs-CZ-Wavenet-A"});

        //Danish (Denmark)
        voice_lang.put("Danish (Denmark)","da-DK");
        voice.put("da-DK",new String[]{"da-DK-Standard-A","da-DK-Standard-C","da-DK-Standard-D","da-DK-Standard-E","da-DK-Wavenet-A","da-DK-Wavenet-C","da-DK-Wavenet-D","da-DK-Wavenet-E"});

        //Dutch (Netherlands)
        voice_lang.put("Dutch (Netherlands)","nl-NL");
        voice.put("nl-NL",new String[]{"nl-NL-Standard-A","nl-NL-Standard-B","nl-NL-Standard-C","nl-NL-Standard-D","nl-NL-Standard-E","nl-NL-Wavenet-A","nl-NL-Wavenet-B","nl-NL-Wavenet-C","nl-NL-Wavenet-D","nl-NL-Wavenet-E"});

        //English (Australia)
        voice_lang.put("English (Australia)","en-AU");
        voice.put("en-AU",new String[]{"en-AU-Standard-A","en-AU-Standard-B","en-AU-Standard-C","en-AU-Standard-D","en-AU-Wavenet-A","en-AU-Wavenet-B","en-AU-Wavenet-C","en-AU-Wavenet-D"});

        //English (India)
        voice_lang.put("English (India)","en-IN");
        voice.put("en-IN",new String[]{"en-IN-Standard-A","en-IN-Standard-B","en-IN-Standard-C","en-IN-Standard-D","en-IN-Wavenet-A","en-IN-Wavenet-B","en-IN-Wavenet-C","en-IN-Wavenet-D"});

        //English (UK)
        voice_lang.put("English (UK)","en-GB");
        voice.put("en-GB",new String[]{"en-GB-Standard-A","en-GB-Standard-B","en-GB-Standard-C","en-GB-Standard-D","en-GB-Standard-F","en-GB-Wavenet-A","en-GB-Wavenet-B","en-GB-Wavenet-C","en-GB-Wavenet-D","en-GB-Wavenet-F"});

        //English (US)
        voice_lang.put("English (US)","en-US");
        voice.put("en-US",new String[]{"en-US-Standard-B","en-US-Standard-C","en-US-Standard-D","en-US-Standard-E","en-US-Standard-G","en-US-Standard-H","en-US-Standard-I","en-US-Standard-J","en-US-Wavenet-A","en-US-Wavenet-B","en-US-Wavenet-C","en-US-Wavenet-D","en-US-Wavenet-E","en-US-Wavenet-F","en-US-Wavenet-I","en-US-Wavenet-J"});

        //Filipino (Philippines)
        voice_lang.put("Filipino (Philippines)","fil-PH");
        voice.put("fil-PH",new String[]{"fil-PH-Standard-A","fil-PH-Standard-B","fil-PH-Standard-C","fil-PH-Standard-D","fil-PH-Wavenet-A","fil-PH-Wavenet-B","fil-PH-Wavenet-C","fil-PH-Wavenet-D"});

        //Finnish (Finland)
        voice_lang.put("Finnish (Finland)","fi-FI");
        voice.put("fi-FI",new String[]{"fi-FI-Standard-A","fi-FI-Wavenet-B"});

        //French (Canada)
        voice_lang.put("French (Canada)","fr-CA");
        voice.put("fr-CA",new String[]{"fr-CA-Standard-A","fr-CA-Standard-B","fr-CA-Standard-C","fr-CA-Standard-D","fr-CA-Wavenet-A","fr-CA-Wavenet-B","fr-CA-Wavenet-C","fr-CA-Wavenet-D"});

        //French (France)
        voice_lang.put("French (France)","fr-FR");
        voice.put("fr-FR",new String[]{"fr-FR-Standard-A","fr-FR-Standard-B","fr-FR-Standard-C","fr-FR-Standard-D","fr-FR-Standard-E","fr-FR-Wavenet-A","fr-FR-Wavenet-B","fr-FR-Wavenet-C","fr-FR-Wavenet-D","fr-FR-Wavenet-E"});

        //German (Germany)
        voice_lang.put("German (Germany)","de-DE");
        voice.put("de-DE",new String[]{"de-DE-Standard-A","de-DE-Standard-B","de-DE-Standard-E","de-DE-Standard-F","de-DE-Wavenet-A","de-DE-Wavenet-B","de-DE-Wavenet-C","de-DE-Wavenet-D","de-DE-Wavenet-E","de-DE-Wavenet-F"});

        //Greek (Greece)
        voice_lang.put("Greek (Greece)","el-GR");
        voice.put("el-GR",new String[]{"el-GR-Standard-A","el-GR-Wavenet-A"});

        //Gujarati (India)
        voice_lang.put("Gujarati (India)","gu-IN");
        voice.put("gu-IN",new String[]{"gu-IN-Standard-A","gu-IN-Standard-B"});

        //Hindi (India)
        voice_lang.put("Hindi (India)","hi-IN");
        voice.put("hi-IN",new String[]{"hi-IN-Standard-A","hi-IN-Standard-B","hi-IN-Standard-C","hi-IN-Standard-D","hi-IN-Wavenet-A","hi-IN-Wavenet-B","hi-IN-Wavenet-C","hi-IN-Wavenet-D"});

        //Hungarian (Hungary)
        voice_lang.put("Hungarian (Hungary)","hu-HU");
        voice.put("hu-HU",new String[]{"hu-HU-Standard-A","hu-HU-Wavenet-A"});

        //Indonesian (Indonesia)
        voice_lang.put("Indonesian (Indonesia)","id-ID");
        voice.put("id-ID",new String[]{"id-ID-Standard-A","id-ID-Standard-B","id-ID-Standard-C","id-ID-Standard-D","id-ID-Wavenet-A","id-ID-Wavenet-B","id-ID-Wavenet-C","id-ID-Wavenet-D"});

        //Italian (Italy)
        voice_lang.put("Italian (Italy)","it-IT");
        voice.put("it-IT",new String[]{"it-IT-Standard-A","it-IT-Standard-B","it-IT-Standard-C","it-IT-Standard-D","it-IT-Wavenet-A","it-IT-Wavenet-B","it-IT-Wavenet-C","it-IT-Wavenet-D"});

        //Japanese (Japan)
        voice_lang.put("Japanese (Japan)","ja-JP");
        voice.put("ja-JP",new String[]{"ja-JP-Standard-A","ja-JP-Standard-B","ja-JP-Standard-C","ja-JP-Standard-D","ja-JP-Wavenet-A","ja-JP-Wavenet-B","ja-JP-Wavenet-C","ja-JP-Wavenet-D"});

        //Kannada (India)
        voice_lang.put("Kannada (India)","kn-IN");
        voice.put("kn-IN",new String[]{"kn-IN-Standard-A","kn-IN-Standard-B"});

        //Korean (South Korea)
        voice_lang.put("Korean (South Korea","ko-KR");
        voice.put("ko-KR",new String[]{"ko-KR-Standard-A","ko-KR-Standard-B","ko-KR-Standard-C","ko-KR-Standard-D","ko-KR-Wavenet-A","ko-KR-Wavenet-B","ko-KR-Wavenet-C","ko-KR-Wavenet-D"});

        //Malayalam (India)
        voice_lang.put("Malayalam (India)","ml-IN");
        voice.put("ml-IN",new String[]{"ml-IN-Standard-A","ml-IN-Standard-B"});

        //Mandarin Chinese (China)
        voice_lang.put("Mandarin Chinese (China)","cmn-CN");
        voice.put("cmn-CN",new String[]{"cmn-CN-Standard-A","cmn-CN-Standard-B","cmn-CN-Standard-C","cmn-CN-Standard-D","cmn-CN-Wavenet-A","cmn-CN-Wavenet-B","cmn-CN-Wavenet-C","cmn-CN-Wavenet-D"});

        //Mandarin Chinese (Taiwan)
        voice_lang.put("Mandarin Chinese (Taiwan)","cmn-TW");
        voice.put("cmn-TW",new String[]{"cmn-TW-Standard-A","cmn-TW-Standard-B","cmn-TW-Standard-C","cmn-TW-Wavenet-A","cmn-TW-Wavenet-B","cmn-TW-Wavenet-C"});

        //Norwegian (Norway)
        voice_lang.put("Norwegian (Norway)","nb-NO");
        voice.put("nb-NO",new String[]{"nb-NO-Standard-A","nb-NO-Standard-B","nb-NO-Standard-C","nb-NO-Standard-D","nb-NO-Standard-E","nb-NO-Wavenet-A","nb-NO-Wavenet-B","nb-NO-Wavenet-C","nb-NO-Wavenet-D","nb-NO-Wavenet-E"});

        //Polish (Poland)
        voice_lang.put("Polish (Poland)","pl-PL");
        voice.put("pl-PL",new String[]{"pl-PL-Standard-A","pl-PL-Standard-B","pl-PL-Standard-C","pl-PL-Standard-D","pl-PL-Standard-E","pl-PL-Wavenet-A","pl-PL-Wavenet-B","pl-PL-Wavenet-C","pl-PL-Wavenet-D","pl-PL-Wavenet-E"});

        //Portuguese (Brazil)
        voice_lang.put("Portuguese (Brazil)","pt-BR");
        voice.put("pt-BR",new String[]{"pt-BR-Standard-A","pt-BR-Wavenet-A"});

        //Portuguese (Portugal)
        voice_lang.put("Portuguese (Portugal)","pt-PT");
        voice.put("pt-PT",new String[]{"pt-PT-Standard-A","pt-PT-Standard-B","pt-PT-Standard-C","pt-PT-Standard-D","pt-PT-Wavenet-A","pt-PT-Wavenet-B","pt-PT-Wavenet-C","pt-PT-Wavenet-D"});

        //Russian (Russia)
        voice_lang.put("Russian (Russia)","ru-RU");
        voice.put("ru-RU",new String[]{"ru-RU-Standard-A","ru-RU-Standard-B","ru-RU-Standard-C","ru-RU-Standard-D","ru-RU-Standard-E","ru-RU-Wavenet-A","ru-RU-Wavenet-B","ru-RU-Wavenet-C","ru-RU-Wavenet-D","ru-RU-Wavenet-E"});

        //Slovak (Slovakia)
        voice_lang.put("Slovak (Slovakia)","sk-SK");
        voice.put("sk-SK",new String[]{"sk-SK-Standard-A","sk-SK-Wavenet-A"});

        //Spanish (Spain)
        voice_lang.put("Spanish (Spain)","es-ES");
        voice.put("es-ES",new String[]{"es-ES-Standard-A"});

        //Swedish (Sweden)
        voice_lang.put("Swedish (Sweden)","sv-SE");
        voice.put("sv-SE",new String[]{"sv-SE-Standard-A","sv-SE-Wavenet-A"});

        //Tamil (India)
        voice_lang.put("Tamil (India)","ta-IN");
        voice.put("ta-IN",new String[]{"ta-IN-Standard-A","ta-IN-Standard-B"});

        //Telugu (India)
        voice_lang.put("Telugu (India)","te-IN");
        voice.put("te-IN",new String[]{"te-IN-Standard-A","te-IN-Standard-B"});

        //Thai (Thailand)
        voice_lang.put("Thai (Thailand)","th-TH");
        voice.put("th-TH",new String[]{"th-TH-Standard-A"});

        //Turkish (Turkey)
        voice_lang.put("Turkish (Turkey)","tr-TR");
        voice.put("tr-TR",new String[]{"tr-TR-Standard-A","tr-TR-Standard-B","tr-TR-Standard-C","tr-TR-Standard-D","tr-TR-Standard-E","tr-TR-Wavenet-A","tr-TR-Wavenet-B","tr-TR-Wavenet-C","tr-TR-Wavenet-D","tr-TR-Wavenet-E"});

        //Ukrainian (Ukraine)
        voice_lang.put("Ukrainian (Ukraine)","uk-UA");
        voice.put("uk-UA",new String[]{"uk-UA-Standard-A","uk-UA-Wavenet-A"});

        //Vietnamese (Vietnam)
        voice_lang.put("Vietnamese (Vietnam)","vi-VN");
        voice.put("vi-VN",new String[]{"vi-VN-Standard-A","vi-VN-Standard-B","vi-VN-Standard-C","vi-VN-Standard-D","vi-VN-Wavenet-A","vi-VN-Wavenet-B","vi-VN-Wavenet-C","vi-VN-Wavenet-D"});




    }

}
