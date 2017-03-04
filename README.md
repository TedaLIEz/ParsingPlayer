# ParsingPlayer

ParsingPlayer is an Android video library based on [IjkPlayer](https://github.com/Bilibili/ijkplayer), playing video from Youku or other video sites.

<img src="/screenshots/1.png" alt="screenshot" title="screenshot" width="250" height="436" />
<img src="/screenshots/2.png" alt="screenshot" title="screenshot" width="436" height="250" />

# Gradle Dependency

[![Build Status](https://travis-ci.org/TedaLIEz/ParsingPlayer.svg?branch=master)](https://travis-ci.org/TedaLIEz/ParsingPlayer)
[![License: LGPL v2.1](https://img.shields.io/badge/license-LGPL%20v2.1-blue.svg)](http://www.gnu.org/licenses/lgpl-2.1)
[![GitHub release](https://img.shields.io/badge/release-2.0.4-blue.svg)](https://github.com/TedaLIEz/ParsingPlayer/releases/latest)



The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/inquiry/view).
jCenter is the default Maven repository used by Android Studio.

### Dependency

Add this to your module's `build.gradle` file (make sure the version matches the last [release](https://github.com/TedaLIEz/ParsingPlayer/releases/latest)):

```gradle
dependencies {
    // ... other dependencies
    compile 'com.uniquestudio:parsingplayer:2.0.4'
}
```

---

# Table of Contents

1. [Quick Setup](https://github.com/TedaLIEz/ParsingPlayer#quick-setup)
2. [License](https://github.com/TedaLIEz/ParsingPlayer#license)


# Quick Setup

```java
public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play("http://v.youku.com/v_show/id_XMjUyNDIxNjAwNA==.html");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.onDestroy();
    }
}
```

---


# License
```bash
Copyright (c) 2017 UniqueStudio
Licensed under LGPLv2.1 or later
```

# No Commercial Use
Although this project is is licensed under LGPLv2.1,but commercial use is forbidden.As we parse video's address,
we have no idea whether commercial use is legal
