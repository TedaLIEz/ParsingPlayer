# ParsingPlayer

ParsingPlayer is an Android video library based on [IjkPlayer](https://github.com/Bilibili/ijkplayer), playing video from Youku or other video sites.

# Gradle Dependency

[![Build Status](https://travis-ci.org/TedaLIEz/ParsingPlayer.svg?branch=master)](https://travis-ci.org/TedaLIEz/ParsingPlayer)
[![License: LGPL v2](https://img.shields.io/badge/license-LGPL%20v2-blue.svg)](http://www.gnu.org/licenses/lgpl-2.0)
[![GitHub release](https://img.shields.io/github/release/qubyte/rubidium.svg)](https://github.com/TedaLIEz/ParsingPlayer/releases/latest)



The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/inquiry/view).
jCenter is the default Maven repository used by Android Studio.

### Dependency

Add this to your module's `build.gradle` file (make sure the version matches the last [release](https://github.com/TedaLIEz/ParsingPlayer/releases/latest)):

```gradle
dependencies {
    // ... other dependencies
    compile 'com.uniquestudio:parsingplayer:2.0.0'
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


#License
```bash
Copyright (c) 2017 UniqueStudio
Licensed under LGPLv2.1 or later
````

# No Commercial Use
Although this project is is licensed under LGPLv2.1,but commercial use is forbidden.As we parse video's address,
we have no idea whether commercial use is legal
