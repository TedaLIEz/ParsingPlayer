package com.hustunique.jianguo.parsingplayer;

/**
 * Created by JianGuo on 1/17/17.
 * Constant used in unit test
 */

public class TestConstant {
    public static final String YOUKU_URL_1 = "http://v.youku.com/v_show/id_XMjQ3MzE1NDA3Ng";
    public static final String YOUKU_ERROR_URL_1 ="http://v.youku.com/v_show/id_XNjA1NzA2Njgw.html";
    public static final String UNSUPPORTED_URL_1 = "https://item.taobao.com/item.htm?id=526343056423";
    public static final String YOUKU_JSON_1 = "{\n" +
            "   \"e\":{\n" +
            "      \"desc\":\"\",\n" +
            "      \"provider\":\"play\",\n" +
            "      \"code\":0\n" +
            "   },\n" +
            "   \"data\":{\n" +
            "      \"id\":618288519,\n" +
            "      \"stream\":[\n" +
            "         {\n" +
            "            \"logo\":\"youku\",\n" +
            "            \"audio_lang\":\"default\",\n" +
            "            \"media_type\":\"standard\",\n" +
            "            \"height\":360,\n" +
            "            \"subtitle_lang\":\"default\",\n" +
            "            \"segs\":[\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_00/st/mp4/fileid/0300200100587CFC05924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=b03b7cc416484b2b261f76bc&hd=1&myp=0&ts=618&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"total_milliseconds_audio\":\"617558\",\n" +
            "                  \"fileid\":\"0300200100587CFC05924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"617400\",\n" +
            "                  \"key\":\"b03b7cc416484b2b261f76bc&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"size\":\"23258558\"\n" +
            "               }\n" +
            "            ],\n" +
            "            \"width\":640,\n" +
            "            \"stream_type\":\"3gphd\",\n" +
            "            \"milliseconds_video\":617400,\n" +
            "            \"drm_type\":\"default\",\n" +
            "            \"transfer_mode\":\"http\",\n" +
            "            \"milliseconds_audio\":617558,\n" +
            "            \"stream_fileid\":\"0300200100587CFC05924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "            \"size\":23258558\n" +
            "         },\n" +
            "         {\n" +
            "            \"logo\":\"youku\",\n" +
            "            \"audio_lang\":\"default\",\n" +
            "            \"media_type\":\"standard\",\n" +
            "            \"height\":360,\n" +
            "            \"subtitle_lang\":\"default\",\n" +
            "            \"segs\":[\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_00/st/flv/fileid/0300020200587CFC30924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=ed910e9b5d02aa6e2412e0d7&hd=0&myp=0&ts=322&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"total_milliseconds_audio\":\"321596\",\n" +
            "                  \"fileid\":\"0300020200587CFC30924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"321600\",\n" +
            "                  \"key\":\"ed910e9b5d02aa6e2412e0d7&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"size\":\"14849560\"\n" +
            "               },\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_01/st/flv/fileid/0300020201587CFC30924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=27184883b9051186282c0ca1&hd=0&myp=0&ts=296&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"total_milliseconds_audio\":\"295915\",\n" +
            "                  \"fileid\":\"0300020201587CFC30924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"295800\",\n" +
            "                  \"key\":\"27184883b9051186282c0ca1&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"size\":\"17517860\"\n" +
            "               }\n" +
            "            ],\n" +
            "            \"width\":640,\n" +
            "            \"stream_type\":\"flvhd\",\n" +
            "            \"milliseconds_video\":617400,\n" +
            "            \"drm_type\":\"default\",\n" +
            "            \"transfer_mode\":\"http\",\n" +
            "            \"milliseconds_audio\":617511,\n" +
            "            \"stream_fileid\":\"0300020200587CFC30924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "            \"size\":32367420\n" +
            "         },\n" +
            "         {\n" +
            "            \"logo\":\"youku\",\n" +
            "            \"audio_lang\":\"default\",\n" +
            "            \"media_type\":\"standard\",\n" +
            "            \"height\":540,\n" +
            "            \"subtitle_lang\":\"default\",\n" +
            "            \"segs\":[\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_00/st/mp4/fileid/0300080200587D0151924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=fa4d7a03e71e35bc282c0ca1&hd=1&myp=0&ts=334&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"total_milliseconds_audio\":\"334135\",\n" +
            "                  \"fileid\":\"0300080200587D0151924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"334134\",\n" +
            "                  \"key\":\"fa4d7a03e71e35bc282c0ca1&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"size\":\"31891916\"\n" +
            "               },\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_01/st/mp4/fileid/0300080201587D0151924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=6c6a9f1e2e472946261f76bc&hd=1&myp=0&ts=283&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"total_milliseconds_audio\":\"283376\",\n" +
            "                  \"fileid\":\"0300080201587D0151924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"283183\",\n" +
            "                  \"key\":\"6c6a9f1e2e472946261f76bc&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"size\":\"35111054\"\n" +
            "               }\n" +
            "            ],\n" +
            "            \"width\":960,\n" +
            "            \"stream_type\":\"mp4hd\",\n" +
            "            \"milliseconds_video\":617317,\n" +
            "            \"drm_type\":\"default\",\n" +
            "            \"transfer_mode\":\"http\",\n" +
            "            \"milliseconds_audio\":617511,\n" +
            "            \"stream_fileid\":\"0300080200587D0151924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "            \"size\":67002970\n" +
            "         },\n" +
            "         {\n" +
            "            \"logo\":\"youku\",\n" +
            "            \"audio_lang\":\"default\",\n" +
            "            \"media_type\":\"standard\",\n" +
            "            \"height\":720,\n" +
            "            \"subtitle_lang\":\"default\",\n" +
            "            \"segs\":[\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_00/st/flv/fileid/0300010400587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=f4ebb940f6d469af282c0ca1&hd=2&myp=0&ts=193&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"total_milliseconds_audio\":\"193260\",\n" +
            "                  \"fileid\":\"0300010400587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"193260\",\n" +
            "                  \"key\":\"f4ebb940f6d469af282c0ca1&sign=26d9d7257075a645ff2e95f2fbe38716\",\n" +
            "                  \"size\":\"33176460\"\n" +
            "               },\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_01/st/flv/fileid/0300010401587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=a07c230d695ba7cd2412e0d7&hd=2&myp=0&ts=205&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"total_milliseconds_audio\":\"204869\",\n" +
            "                  \"fileid\":\"0300010401587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"204871\",\n" +
            "                  \"key\":\"a07c230d695ba7cd2412e0d7&sign=24f134721ea2db7e66ceef6d44d6f61f\",\n" +
            "                  \"size\":\"39404210\"\n" +
            "               },\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_02/st/flv/fileid/0300010402587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=fbaafd429ad9de59282c0ca1&hd=2&myp=0&ts=107&sign=c65c4d6b314f891a70e6c103c06e6b0b\",\n" +
            "                  \"total_milliseconds_audio\":\"107044\",\n" +
            "                  \"fileid\":\"0300010402587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"107040\",\n" +
            "                  \"key\":\"fbaafd429ad9de59282c0ca1&sign=c65c4d6b314f891a70e6c103c06e6b0b\",\n" +
            "                  \"size\":\"25839654\"\n" +
            "               },\n" +
            "               {\n" +
            "                  \"path\":\"http://k.youku.com/player/getFlvPath/sid/0484648765120128e69a7_03/st/flv/fileid/0300010403587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C?k=13427962fa2611ac2412e0d7&hd=2&myp=0&ts=112&sign=0294f5b5967042d295f4eb78c9901730\",\n" +
            "                  \"total_milliseconds_audio\":\"112292\",\n" +
            "                  \"fileid\":\"0300010403587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "                  \"total_milliseconds_video\":\"112146\",\n" +
            "                  \"key\":\"13427962fa2611ac2412e0d7&sign=0294f5b5967042d295f4eb78c9901730\",\n" +
            "                  \"size\":\"28101565\"\n" +
            "               }\n" +
            "            ],\n" +
            "            \"width\":1280,\n" +
            "            \"stream_type\":\"mp4hd2\",\n" +
            "            \"milliseconds_video\":617317,\n" +
            "            \"drm_type\":\"default\",\n" +
            "            \"transfer_mode\":\"http\",\n" +
            "            \"milliseconds_audio\":617465,\n" +
            "            \"stream_fileid\":\"0300010400587D03EF924B2F0630632318E324-644F-6104-140C-0434596BA86C\",\n" +
            "            \"size\":126521889\n" +
            "         }\n" +
            "      ],\n" +
            "      \"preview\":{\n" +
            "         \"timespan\":\"6000\",\n" +
            "         \"thumb\":[\n" +
            "            \"http://g1.ykimg.com/05210002587D04266F0A965F1409D86B\",\n" +
            "            \"http://g1.ykimg.com/05210102587D04266F0A965F1409D86B\"\n" +
            "         ]\n" +
            "      },\n" +
            "      \"uploader\":{\n" +
            "         \"uid\":\"UMzE1NTczOTAyMA==\",\n" +
            "         \"certification\":false,\n" +
            "         \"fan_count\":1150,\n" +
            "         \"crm_level\":4,\n" +
            "         \"username\":\"CPN联邦-爱国实体组织\",\n" +
            "         \"reason\":\"\",\n" +
            "         \"show_brand\":0,\n" +
            "         \"avatar\":{\n" +
            "            \"big\":\"https://r1.ykimg.com/0130391F4856C81B0572192F063063EC1EC6C6-269B-16E6-EBB2-5C2DE7037C34\",\n" +
            "            \"small\":\"https://r1.ykimg.com/0130391F4856C81B05E4DA2F063063A949F99A-A4F0-784B-92DE-0147845812A3\",\n" +
            "            \"middle\":\"https://r1.ykimg.com/0130391F4856C81B0542482F063063055BC5CC-A2D7-35D5-28A6-CC9E75872103\",\n" +
            "            \"large\":\"https://r1.ykimg.com/0130391F4856C81B04FF932F06306300204A59-18FA-53E0-5784-FC9889FCC68D\"\n" +
            "         },\n" +
            "         \"homepage\":\"http://i.youku.com/u/UMzE1NTczOTAyMA==\"\n" +
            "      },\n" +
            "      \"controller\":{\n" +
            "         \"html5_disable\":false,\n" +
            "         \"continuous\":false,\n" +
            "         \"video_capture\":true,\n" +
            "         \"like_disabled\":false,\n" +
            "         \"stream_mode\":1,\n" +
            "         \"app_disable\":false,\n" +
            "         \"download_disable\":false,\n" +
            "         \"share_disable\":false,\n" +
            "         \"circle\":false,\n" +
            "         \"play_mode\":1\n" +
            "      },\n" +
            "      \"security\":{\n" +
            "         \"encrypt_string\":\"NwXYSgsaLrzc1PHD9eJxWtXysEI61wzKXB8=\",\n" +
            "         \"ip\":1939659569\n" +
            "      },\n" +
            "      \"user\":{\n" +
            "         \"uid\":\"\"\n" +
            "      },\n" +
            "      \"network\":{\n" +
            "         \"dma_code\":\"4538\",\n" +
            "         \"area_code\":\"420100\",\n" +
            "         \"country_code\":\"CN\"\n" +
            "      },\n" +
            "      \"video\":{\n" +
            "         \"tags\":[\n" +
            "            \"\"\n" +
            "         ],\n" +
            "         \"logo\":\"https://r1.ykimg.com/05410408587D03F76A0A4004572FFF78\",\n" +
            "         \"published_time\":\"2017-01-17 01:03:56\",\n" +
            "         \"userid\":788934755,\n" +
            "         \"privacy\":\"follower\",\n" +
            "         \"ctype\":\"UGC\",\n" +
            "         \"category_id\":105,\n" +
            "         \"type\":[\n" +
            "            \"video\"\n" +
            "         ],\n" +
            "         \"upload\":\"normal\",\n" +
            "         \"restrict\":0,\n" +
            "         \"title\":\"iPhone 8 新功能搶先看？(中文字幕) CPNTV\",\n" +
            "         \"username\":\"CPN联邦-爱国实体组织\",\n" +
            "         \"source\":1,\n" +
            "         \"seconds\":\"617.00\",\n" +
            "         \"encodeid\":\"XMjQ3MzE1NDA3Ng==\",\n" +
            "         \"category_letter_id\":\"o\",\n" +
            "         \"subcategories\":{\n" +
            "\n" +
            "         },\n" +
            "         \"channel\":{\n" +
            "            \"tail\":[\n" +
            "               \"XMTg2OTk3MDM2NA==\"\n" +
            "            ]\n" +
            "         }\n" +
            "      }\n" +
            "   },\n" +
            "   \"cost\":0.00800000037997961\n" +
            "}";
    public static String YOUKU_ERROR_JSON_1 = "{\"e\":{\"desc\":\"\",\"provider\":\"play\"," +
            "\"code\":0},\"data\":{\"id\":151426670,\"error\":{\"code\":-202," +
            "\"note\":\"该视频已经加密，请<font color=\\\"#FF0000\\\">输入密码<\\/font>\"}," +
            "\"uploader\":{\"uid\":\"UMTI4ODA1NzE0MA==\",\"certification\":false," +
            "\"fan_count\":0,\"crm_level\":0,\"username\":\"FoxJin1006\",\"reason\":\"" +
            "\",\"show_brand\":0,\"avatar\":{\"big\":\"https://static.youku.com/user/im" +
            "g/avatar/80/55.jpg\",\"small\":\"https://static.youku.com/user/img/avatar/30/55.jpg\"" +
            ",\"middle\":\"https://static.youku.com/user/img/avatar/50/55.jpg\"," +
            "\"large\":\"https://static.youku.com/user/img/avatar/310/55.jpg\"}," +
            "\"homepage\":\"http://i.youku.com/u/UMTI4ODA1NzE0MA==\"}," +
            "\"controller\":{\"continuous\":false,\"video_capture\":true," +
            "\"like_disabled\":false,\"stream_mode\":1,\"download_disable\":false," +
            "\"share_disable\":false,\"circle\":false,\"play_mode\":1}," +
            "\"security\":{\"encrypt_string\":\"NwXYSw4aIrjZ1vXI9eJxUNb27Rc11wnOXh4=\"" +
            ",\"ip\":1001157218},\"user\":{\"uid\":\"\"},\"video\":{\"tags\":" +
            "[\"讲座\",\"邢义田\"],\"logo\":\"https://r1.ykimg.com/05410408522963E36A" +
            "0A474F134E4D52\",\"published_time\":\"2013-09-06 13:14:33\",\"userid\":322014285," +
            "\"privacy\":\"password\",\"ctype\":\"UGC\",\"category_id\":87,\"type\":[\"video\"]," +
            "\"upload\":\"normal\",\"restrict\":0,\"title\":\"邢義田复旦讲座之想象中的胡人\\u2014" +
            "从\\u201c左衽孔子\\u201d说起\",\"username\":\"FoxJin1006\",\"source\":1" +
            ",\"seconds\":\"7264.50\",\"encodeid\":\"XNjA1NzA2Njgw\",\"category_letter_id\":" +
            "\"t\",\"subcategories\":[{\"id\":\"252\",\"name\":\"校园课堂\"}]}},\"cost\"" +
            ":0.006000000052154064}";
}
