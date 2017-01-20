package com.hustunique.jianguo.parsingplayer.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by JianGuo on 1/20/17.
 * Custom media controller view for video view.
 */
// TODO: 1/20/17 Custom media controller panel
public class ParsingMediaController extends MediaController implements IMediaController {
    public ParsingMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParsingMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public ParsingMediaController(Context context) {
        super(context);
    }
}
