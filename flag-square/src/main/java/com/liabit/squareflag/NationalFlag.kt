package com.liabit.squareflag

object NationalFlag {

    private val mCountryList = ArrayList<Country>()

    init {
        mCountryList.add(Country("Afghanistan", "AFG", "af", "93", R.drawable.s_afghanistan))
        mCountryList.add(Country("South Africa", "ZAF", "za", "27", R.drawable.s_south_africa))
        mCountryList.add(Country("Albania", "ALB", "al", "355", R.drawable.s_albania))
        mCountryList.add(Country("Algeria", "DZA", "dz", "213", R.drawable.s_algeria))
        mCountryList.add(Country("Germany", "DEU", "de", "49", R.drawable.s_germany))
        mCountryList.add(Country("Andorra", "AND", "ad", "376", R.drawable.s_andorra))
        mCountryList.add(Country("England", "GBE", "gb", "44", R.drawable.s_england))
        mCountryList.add(Country("Angola", "AGO", "ao", "244", R.drawable.s_angola))
        mCountryList.add(Country("Anguilla", "AIA", "ai", "1264", R.drawable.s_anguilla))
        mCountryList.add(Country("Antigua and Barbuda", "ATG", "ag", "1268", R.drawable.s_antigua_and_barbuda))
        mCountryList.add(Country("Saudi Arabia", "SAU", "sa", "966", R.drawable.s_saudi_arabia))
        mCountryList.add(Country("Argentina", "ARG", "ar", "54", R.drawable.s_argentina))
        mCountryList.add(Country("Armenia", "ARM", "am", "374", R.drawable.s_armenia))
        mCountryList.add(Country("Aruba", "ABW", "aw", "297", R.drawable.s_aruba))
        mCountryList.add(Country("Australia", "AUS", "au", "61", R.drawable.s_australia))
        mCountryList.add(Country("Austria", "AUT", "at", "43", R.drawable.s_austria))
        mCountryList.add(Country("Azerbaijan", "AZE", "az", "994", R.drawable.s_azerbaijan))
        mCountryList.add(Country("Bahamas", "BHS", "bs", "1242", R.drawable.s_bahamas))
        mCountryList.add(Country("Bahrain", "BHR", "bh", "973", R.drawable.s_bahrain))
        mCountryList.add(Country("Bangladesh", "BGD", "bd", "880", R.drawable.s_bangladesh))
        mCountryList.add(Country("Barbados", "BRB", "bb", "1246", R.drawable.s_barbados))
        mCountryList.add(Country("Belgium", "BEL", "be", "32", R.drawable.s_belgium))
        mCountryList.add(Country("Belize", "BLZ", "bz", "501", R.drawable.s_belize))
        mCountryList.add(Country("Benin", "BEN", "bj", "229", R.drawable.s_benin))
        mCountryList.add(Country("Bermuda", "BMU", "bm", "1441", R.drawable.s_bermuda))
        mCountryList.add(Country("Bhutan", "BTN", "bt", "975", R.drawable.s_bhutan))
        mCountryList.add(Country("Belarus", "BLR", "by", "375", R.drawable.s_belarus))
        mCountryList.add(Country("Bolivia", "BOL", "bo", "591", R.drawable.s_bolivia))
        mCountryList.add(Country("Bosnia and Herzegovina", "BIH", "ba", "387", R.drawable.s_bosnia_and_herzegovina))
        mCountryList.add(Country("Botswana", "BWA", "bw", "267", R.drawable.s_botswana))
        mCountryList.add(Country("Brazil", "BRA", "br", "55", R.drawable.s_brazil))
        mCountryList.add(Country("Brunei", "BRN", "bn", "673", R.drawable.s_brunei))
        mCountryList.add(Country("Bulgaria", "BGR", "bg", "359", R.drawable.s_bulgaria))
        mCountryList.add(Country("Burkina Faso", "BFA", "bf", "226", R.drawable.s_burkina_faso))
        //////
        mCountryList.add(Country("Burma", "MMR", "mm", "95", R.drawable.s_myanmar))
        mCountryList.add(Country("Burundi", "BDI", "bi", "257", R.drawable.s_burundi))
        mCountryList.add(Country("Cambodia", "KHM", "kh", "855", R.drawable.s_cambodia))
        mCountryList.add(Country("Cameroon", "CMR", "cm", "237", R.drawable.s_cameroon))
        mCountryList.add(Country("Canada", "CAN", "ca", "1", R.drawable.s_canada))
        mCountryList.add(Country("Cape Verde", "CPV", "cv", "238", R.drawable.s_cape_verde))
        mCountryList.add(Country("Chile", "CHL", "cl", "56", R.drawable.s_chile))
        mCountryList.add(Country("China", "CHN", "cn", "86", R.drawable.s_china))
        //////
        mCountryList.add(Country("Cyprus", "CYP", "cy", "357", R.drawable.s_cyprus))
        mCountryList.add(Country("Colombia", "COL", "co", "57", R.drawable.s_colombia))
        mCountryList.add(Country("Comoros", "COM", "km", "269", R.drawable.s_comoros))
        mCountryList.add(Country("Republic of the Congo", "COG", "cg", "242", R.drawable.s_republic_of_the_congo))
        mCountryList.add(Country("South Korea", "KOR", "kr", "82", R.drawable.s_south_korea))
        mCountryList.add(Country("Costa Rica", "CRC", "cr", "506", R.drawable.s_costa_rica))
        mCountryList.add(Country("Ivory Coast", "CIV", "ci", "225", R.drawable.s_ivory_coast))
        mCountryList.add(Country("Croatia", "HRV", "hr", "385", R.drawable.s_croatia))
        mCountryList.add(Country("Cuba", "CUB", "cu", "53", R.drawable.s_cuba))
        mCountryList.add(Country("Denmark", "DNK", "dk", "45", R.drawable.s_denmark))
        mCountryList.add(Country("Djibouti", "DJI", "dj", "253", R.drawable.s_djibouti))
        mCountryList.add(Country("Dominica", "DMA", "dm", "1767", R.drawable.s_dominica))
        mCountryList.add(Country("Scotland", "GBS", "gb", "44", R.drawable.s_scotland))
        mCountryList.add(Country("Egypt", "EGY", "eg", "20", R.drawable.s_egypt))
        mCountryList.add(Country("United Arab Emirates", "ARE", "ae", "971", R.drawable.s_united_arab_emirates))
        mCountryList.add(Country("Ecuador", "ECU", "ec", "593", R.drawable.s_ecuador))
        mCountryList.add(Country("Eritrea", "ERI", "er", "291", R.drawable.s_eritrea))
        mCountryList.add(Country("Spain", "ESP", "es", "34", R.drawable.s_spain))
        mCountryList.add(Country("Estonia", "EST", "ee", "372", R.drawable.s_estonia))
        mCountryList.add(Country("Ethiopia", "ETH", "et", "251", R.drawable.s_ethiopia))
        mCountryList.add(Country("Finland", "FIN", "fi", "358", R.drawable.s_finland))
        mCountryList.add(Country("France", "FRA", "fr", "33", R.drawable.s_france))
        mCountryList.add(Country("Gabon", "GAB", "ga", "241", R.drawable.s_gabon))
        mCountryList.add(Country("Gambia", "GMB", "gm", "220", R.drawable.s_gambia))
        mCountryList.add(Country("Georgia", "GEO", "ge", "995", R.drawable.s_georgia))
        mCountryList.add(Country("Ghana", "GHA", "gh", "233", R.drawable.s_ghana))
        mCountryList.add(Country("Gibraltar", "GIB", "gi", "350", R.drawable.s_gibraltar))
        mCountryList.add(Country("Greece", "GRC", "gr", "30", R.drawable.s_greece))
        mCountryList.add(Country("Grenada", "GRD", "gd", "1473", R.drawable.s_grenada))
        mCountryList.add(Country("Greenland", "GRL", "gl", "299", R.drawable.s_greenland))
        mCountryList.add(Country("Guatemala", "GTM", "gt", "502", R.drawable.s_guatemala))
        mCountryList.add(Country("Guinea", "GIN", "gn", "224", R.drawable.s_guinea))
        mCountryList.add(Country("Equatorial Guinea", "GNQ", "gq", "240", R.drawable.s_equatorial_guinea))
        mCountryList.add(Country("Guinea-Bissau", "GNB", "gw", "245", R.drawable.s_guinea_bissau))
        mCountryList.add(Country("Haiti", "HTI", "ht", "509", R.drawable.s_haiti))
        mCountryList.add(Country("Honduras", "HND", "hn", "504", R.drawable.s_honduras))
        mCountryList.add(Country("Hong Kong", "HKG", "hk", "852", R.drawable.s_hong_kong))
        mCountryList.add(Country("Hungary", "HUN", "hu", "36", R.drawable.s_hungary))
        mCountryList.add(Country("Christmas Island", "CXR", "cx", "61", R.drawable.s_christmas_island))
        mCountryList.add(Country("Mauritius", "MUS", "mu", "230", R.drawable.s_mauritius))
        mCountryList.add(Country("Solomon Islands", "SLB", "sb", "677", R.drawable.s_solomon_islands))
        mCountryList.add(Country("Cayman Islands", "CYM", "ky", "1345", R.drawable.s_cayman_islands))
        mCountryList.add(Country("Cook Islands", "COK", "ck", "682", R.drawable.s_cook_islands))
        mCountryList.add(Country("Falkland Islands", "FLK", "fk", "500", R.drawable.s_falkland_islands))
        //////
        mCountryList.add(Country("Faroe Islands", "FRO", "fo", "298", R.drawable.s_faroe_islands))
        mCountryList.add(Country("Fiji", "FJI", "fj", "679", R.drawable.s_fiji))
        //////
        mCountryList.add(Country("Mariana Island", "MNP", "mp", "1670", R.drawable.s_northern_marianas_islands))
        //////
        mCountryList.add(Country("Turks and Caicos Islands", "TCA", "tc", "1649", R.drawable.s_turks_and_caicos))
        mCountryList.add(Country("British Virgin Islands", "VGB", "vg", "1284", R.drawable.s_british_virgin_islands))
        mCountryList.add(Country("Virgin Islands", "VIR", "vi", "1340", R.drawable.s_virgin_islands))
        mCountryList.add(Country("India", "IND", "in", "91", R.drawable.s_india))
        mCountryList.add(Country("Indonesia", "IDN", "id", "62", R.drawable.s_indonesia))
        mCountryList.add(Country("Iraq", "IRQ", "iq", "964", R.drawable.s_iraq))
        mCountryList.add(Country("Ireland", "IRL", "ie", "353", R.drawable.s_ireland))
        mCountryList.add(Country("Israel", "ISR", "il", "972", R.drawable.s_israel))
        mCountryList.add(Country("Italy", "ITA", "it", "39", R.drawable.s_italy))
        mCountryList.add(Country("Jamaica", "JAM", "jm", "1876", R.drawable.s_jamaica))
        mCountryList.add(Country("Japan", "JPN", "jp", "81", R.drawable.s_japan))
        mCountryList.add(Country("Jordan", "JOR", "jo", "962", R.drawable.s_jordan))
        mCountryList.add(Country("Kazakhstan", "KAZ", "kz", "7", R.drawable.s_kazakhstan))
        mCountryList.add(Country("Kenya", "KEN", "ke", "254", R.drawable.s_kenya))
        mCountryList.add(Country("Kyrgyzstan", "KGZ", "kg", "996", R.drawable.s_kyrgyzstan))
        mCountryList.add(Country("Kiribati", "KIR", "ki", "686", R.drawable.s_kiribati))
        mCountryList.add(Country("Kosovo", "XKX", "xk", "381", R.drawable.s_kosovo))
        //////
        mCountryList.add(Country("Kuwait", "KWT", "kw", "965", R.drawable.s_kwait))
        mCountryList.add(Country("Lesotho", "LSO", "ls", "266", R.drawable.s_lesotho))
        mCountryList.add(Country("Latvia", "LVA", "lv", "371", R.drawable.s_latvia))
        mCountryList.add(Country("Lebanon", "LBN", "lb", "961", R.drawable.s_lebanon))
        mCountryList.add(Country("Liberia", "LBR", "lr", "231", R.drawable.s_liberia))
        mCountryList.add(Country("Libya", "LBY", "ly", "218", R.drawable.s_libya))
        mCountryList.add(Country("Liechtenstein", "LIE", "li", "423", R.drawable.s_liechtenstein))
        mCountryList.add(Country("Lithuania", "LTU", "lt", "370", R.drawable.s_lithuania))
        mCountryList.add(Country("Luxembourg", "LUX", "lu", "352", R.drawable.s_luxembourg))
        //////
        mCountryList.add(Country("Macau", "MAC", "mo", "853", R.drawable.s_macao))
        //////
        mCountryList.add(Country("Macedonia", "MKD", "mk", "389", R.drawable.s_republic_of_macedonia))
        mCountryList.add(Country("Madagascar", "MDG", "mg", "261", R.drawable.s_madagascar))
        //////
        mCountryList.add(Country("Malaysia", "MYS", "my", "60", R.drawable.s_malasya))
        mCountryList.add(Country("Malawi", "MWI", "mw", "265", R.drawable.s_malawi))
        mCountryList.add(Country("Maldives", "MDV", "mv", "960", R.drawable.s_maldives))
        mCountryList.add(Country("Mali", "MLI", "ml", "223", R.drawable.s_mali))
        mCountryList.add(Country("Malta", "MLT", "mt", "356", R.drawable.s_malta))
        mCountryList.add(Country("Morocco", "MAR", "ma", "212", R.drawable.s_morocco))
        mCountryList.add(Country("Martinique", "MTQ", "fr", "596", R.drawable.s_martinique))
        mCountryList.add(Country("Mauritania", "MRT", "mr", "222", R.drawable.s_mauritania))
        //////
        mCountryList.add(Country("Mayotte", "MYT", "fr", "262", R.drawable.s_france))
        mCountryList.add(Country("Mexico", "MEX", "mx", "52", R.drawable.s_mexico))
        mCountryList.add(Country("Micronesia", "FSM", "fm", "691", R.drawable.s_micronesia))
        mCountryList.add(Country("Monaco", "MCO", "mc", "377", R.drawable.s_monaco))
        mCountryList.add(Country("Mongolia", "MNG", "mn", "976", R.drawable.s_mongolia))
        mCountryList.add(Country("Montenegro", "MNE", "me", "382", R.drawable.s_montenegro))
        mCountryList.add(Country("Montserrat", "MSR", "ms", "1664", R.drawable.s_montserrat))
        mCountryList.add(Country("Mozambique", "MOZ", "mz", "258", R.drawable.s_mozambique))
        mCountryList.add(Country("Namibia", "NAM", "na", "264", R.drawable.s_namibia))
        mCountryList.add(Country("Nauru", "NRU", "nr", "674", R.drawable.s_nauru))
        mCountryList.add(Country("Nepal", "NPL", "np", "977", R.drawable.s_nepal))
        mCountryList.add(Country("Nicaragua", "NIC", "ni", "505", R.drawable.s_nicaragua))
        mCountryList.add(Country("Niger", "NER", "ne", "227", R.drawable.s_niger))
        mCountryList.add(Country("Nigeria", "NGA", "ng", "234", R.drawable.s_nigeria))
        mCountryList.add(Country("Niue", "NUI", "nu", "683", R.drawable.s_niue))
        mCountryList.add(Country("Norway", "NOR", "no", "47", R.drawable.s_norway))
        //////
        mCountryList.add(Country("New Caledonia", "NCL", "nc", "687", R.drawable.s_france))
        mCountryList.add(Country("New Zealand", "NZL", "nz", "64", R.drawable.s_new_zealand))
        mCountryList.add(Country("Oman", "OMN", "om", "968", R.drawable.s_oman))
        mCountryList.add(Country("Uganda", "UGA", "ug", "256", R.drawable.s_uganda))
        //////
        mCountryList.add(Country("Uzbekistan", "UZB", "uz", "998", R.drawable.s_uzbekistn))
        mCountryList.add(Country("Pakistan", "PAK", "pk", "92", R.drawable.s_pakistan))
        mCountryList.add(Country("Palau", "PLW", "pw", "680", R.drawable.s_palau))
        mCountryList.add(Country("Palestine", "PSE", "ps", "970", R.drawable.s_palestine))
        mCountryList.add(Country("Panama", "PAN", "pa", "507", R.drawable.s_panama))
        mCountryList.add(Country("Paraguay", "PRY", "py", "595", R.drawable.s_paraguay))
        mCountryList.add(Country("Wales", "GBW", "gb", "44", R.drawable.s_wales))
        mCountryList.add(Country("Netherlands", "NLD", "nl", "31", R.drawable.s_netherlands))
        mCountryList.add(Country("Peru", "PER", "pe", "51", R.drawable.s_peru))
        mCountryList.add(Country("Philippines", "PHL", "ph", "63", R.drawable.s_philippines))
        mCountryList.add(Country("Poland", "POL", "pl", "48", R.drawable.s_poland))
        mCountryList.add(Country("French Polynesia", "PYF", "fr", "689", R.drawable.s_french_polynesia))
        mCountryList.add(Country("Puerto Rico", "PRI", "pr", "1", R.drawable.s_puerto_rico))
        mCountryList.add(Country("Portugal", "PRT", "pt", "351", R.drawable.s_portugal))
        mCountryList.add(Country("Qatar", "QAT", "qa", "974", R.drawable.s_qatar))
        mCountryList.add(Country("Central African Republic", "CAF", "cf", "236", R.drawable.s_central_african_republic))
        mCountryList.add(Country("Moldova", "MDA", "md", "373", R.drawable.s_moldova))
        //////
        mCountryList.add(Country("Democratic Republic of the Congo", "COD", "cd", "243", R.drawable.s_democratic_republic_of_congo))
        mCountryList.add(Country("Dominican Republic", "DOM", "do", "1809", R.drawable.s_dominican_republic))
        mCountryList.add(Country("Iran", "IRN", "ir", "98", R.drawable.s_iran))
        mCountryList.add(Country("North Korea", "PRK", "kp", "850", R.drawable.s_north_korea))
        mCountryList.add(Country("Laos", "LAO", "la", "856", R.drawable.s_laos))
        mCountryList.add(Country("Czech Republic", "CZE", "cz", "420", R.drawable.s_czech_republic))
        mCountryList.add(Country("Romania", "ROU", "ro", "40", R.drawable.s_romania))
        mCountryList.add(Country("United Kingdom", "GBR", "gb", "44", R.drawable.s_united_kingdom))
        mCountryList.add(Country("Russia", "RUS", "ru", "7", R.drawable.s_russia))
        mCountryList.add(Country("Rwanda", "RWA", "rw", "250", R.drawable.s_rwanda))
        mCountryList.add(Country("San Marino", "SMR", "sm", "378", R.drawable.s_san_marino))
        mCountryList.add(Country("El Salvador", "SLV", "sv", "503", R.drawable.s_el_salvador))
        mCountryList.add(Country("Samoa", "WSM", "ws", "685", R.drawable.s_samoa))
        mCountryList.add(Country("Senegal", "SEN", "sn", "221", R.drawable.s_senegal))
        mCountryList.add(Country("Serbia", "SRB", "rs", "381", R.drawable.s_serbia))
        mCountryList.add(Country("Seychelles", "SYC", "sc", "248", R.drawable.s_seychelles))
        mCountryList.add(Country("Sierra Leone", "SLE", "sl", "232", R.drawable.s_sierra_leone))
        mCountryList.add(Country("Singapore", "SGP", "sg", "65", R.drawable.s_singapore))
        mCountryList.add(Country("Slovakia", "SVK", "sk", "421", R.drawable.s_slovakia))
        mCountryList.add(Country("Slovenia", "SVN", "si", "386", R.drawable.s_slovenia))
        mCountryList.add(Country("Somalia", "SOM", "so", "252", R.drawable.s_somalia))
        mCountryList.add(Country("Sudan", "SDN", "sd", "249", R.drawable.s_sudan))
        mCountryList.add(Country("Sri Lanka", "LKA", "lk", "94", R.drawable.s_sri_lanka))
        //////
        mCountryList.add(Country("St. Kitts and Nevis", "KNA", "kn", "1869", R.drawable.s_saint_kitts_and_nevis))
        //////
        mCountryList.add(Country("St. Vincent and the Grenadines", "VCT", "vc", "1784", R.drawable.s_st_vincent_and_the_grenadines))
        mCountryList.add(Country("Sweden", "SWE", "se", "46", R.drawable.s_sweden))
        mCountryList.add(Country("Switzerland", "CHE", "ch", "41", R.drawable.s_switzerland))
        mCountryList.add(Country("Swaziland", "SWZ", "sz", "268", R.drawable.s_swaziland))
        //////
        mCountryList.add(Country("Surinam", "SUR", "sr", "597", R.drawable.s_suriname))
        mCountryList.add(Country("Syria", "SYR", "sy", "963", R.drawable.s_syria))
        //////
        mCountryList.add(Country("São Tomé and Príncipe", "STP", "st", "239", R.drawable.s_sao_tome_and_prince))
        mCountryList.add(Country("Tajikistan", "TJK", "tj", "992", R.drawable.s_tajikistan))
        mCountryList.add(Country("Taiwan", "TWN", "tw", "886", R.drawable.s_taiwan))
        mCountryList.add(Country("Tanzania", "TZA", "tz", "255", R.drawable.s_tanzania))
        mCountryList.add(Country("Chad", "TCD", "td", "235", R.drawable.s_chad))
        //////*
        mCountryList.add(Country("Diego Garcia", "IOT", "io", "246", R.drawable.s_diego_garcia))
        mCountryList.add(Country("Thailand", "THA", "th", "66", R.drawable.s_thailand))
        mCountryList.add(Country("East Timor", "TLS", "tl", "670", R.drawable.s_east_timor))
        mCountryList.add(Country("Togo", "TGO", "tg", "228", R.drawable.s_togo))
        mCountryList.add(Country("Tonga", "TON", "to", "676", R.drawable.s_tonga))
        mCountryList.add(Country("Trinidad and Tobago", "TTO", "tt", "1868", R.drawable.s_trinidad_and_tobago))
        mCountryList.add(Country("Tunisia", "TUN", "tn", "216", R.drawable.s_tunisia))
        mCountryList.add(Country("Turkmenistan", "TKM", "tm", "993", R.drawable.s_turkmenistan))
        mCountryList.add(Country("Turkey", "TUR", "tr", "90", R.drawable.s_turkey))
        mCountryList.add(Country("Tuvalu", "TUV", "tv", "688", R.drawable.s_tuvalu))
        mCountryList.add(Country("Ukraine", "UKR", "ua", "380", R.drawable.s_ukraine))
        //////
        mCountryList.add(Country("United States Of America", "USA", "us", "1", R.drawable.s_united_states))
        mCountryList.add(Country("Uruguay", "URY", "uy", "598", R.drawable.s_uruguay))
        mCountryList.add(Country("Vanuatu", "VUT", "vu", "678", R.drawable.s_vanuatu))
        mCountryList.add(Country("Vatican City", "VAT", "va", "39", R.drawable.s_vatican_city))
        mCountryList.add(Country("Venezuela", "VEN", "ve", "58", R.drawable.s_venezuela))
        mCountryList.add(Country("Vietnam", "VNM", "vn", "84", R.drawable.s_vietnam))
        mCountryList.add(Country("Yemen", "YEM", "ye", "967", R.drawable.s_yemen))
        mCountryList.add(Country("Zambia", "ZMB", "zm", "260", R.drawable.s_zambia))
        mCountryList.add(Country("Zimbabwe", "ZWE", "zw", "263", R.drawable.s_zimbabwe))


    }

    fun getFlagResourceId(alpha2: String): Int {
        for (country in mCountryList) {
            if (country.alpha2.equals(alpha2, true) || country.alpha3.equals(alpha2, true)) {
                return country.flag
            }
        }
        return R.drawable.s_nono
    }

    fun getFlagResourceIdByDialingCode(dialingCode: String): Int {
        for (country in mCountryList) {
            if (country.dialingCode == dialingCode.replace("+", "").replace(" ", "")) {
                return country.flag
            }
        }
        return R.drawable.s_nono
    }

}