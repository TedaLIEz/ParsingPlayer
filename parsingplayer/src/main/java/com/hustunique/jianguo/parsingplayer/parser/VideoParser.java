

package com.hustunique.jianguo.parsingplayer.parser;

import android.support.annotation.NonNull;

import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.jianguo.parsingplayer.parser.extractor.IExtractor;
import com.hustunique.jianguo.parsingplayer.parser.extractor.Youku;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JianGuo on 1/16/17.
 * Parser extracting video info from a given string.
 */

public class VideoParser {
    private String[] urlRegexArray = {Youku.VALID_URL};
    private Class[] extractorArray = {Youku.class};
    private IExtractor iExtractor;

    private IExtractor createExtractor(String url) throws IllegalAccessException, InstantiationException {
        Pattern pattern;
        Matcher matcher;
        for (int i=0;i<urlRegexArray.length;i++){
            pattern = Pattern.compile(urlRegexArray[i]);
            matcher = pattern.matcher(url);
            if (matcher.find()){
                return  (IExtractor) extractorArray[i].newInstance();
            }
        }
        throw new RuntimeException("This url is not valid");
    }

    public VideoInfo parse(String url){
        try {
            iExtractor = createExtractor(url);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return iExtractor.extract(url);
    }
}
