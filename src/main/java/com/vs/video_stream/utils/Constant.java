package com.vs.video_stream.utils;

public class Constant {

    public static final String VIDEO_WITH_AUDIO_COMMAND = "ffmpeg -i \"%s\" -preset fast -g 48 -sc_threshold 0 "
            + "-map 0:v:0 -map 0:a:0 -s:v:0 1920x1080 -b:v:0 6000k -maxrate:v:0 5350k -bufsize:v:0 7500k -b:a:0 192k "
            + "-map 0:v:0 -map 0:a:0 -s:v:1 1280x720 -b:v:1 2800k -maxrate:v:1 2996k -bufsize:v:1 4200k -b:a:1 128k "
            + "-map 0:v:0 -map 0:a:0 -s:v:2 854x480 -b:v:2 1400k -maxrate:v:2 1498k -bufsize:v:2 2100k -b:a:2 96k "
            + "-map 0:v:0 -map 0:a:0 -s:v:3 640x360 -b:v:3 800k -maxrate:v:3 856k -bufsize:v:3 1200k -b:a:3 64k "
            + "-f hls -hls_time 10 -hls_playlist_type vod -hls_segment_filename \"%s/v%%v_segment_%%03d.ts\" "
            + "-var_stream_map \"v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3\" -master_pl_name \"%s\" \"%s/v%%v.m3u8\"";

    public static final String VIDEO_WITHOUT_AUDIO_COMMAND = "ffmpeg -i \"%s\" -preset fast -g 48 -sc_threshold 0 "
            + "-map 0:v:0 -s:v:0 1920x1080 -b:v:0 6000k -maxrate:v:0 5350k -bufsize:v:0 7500k "
            + "-map 0:v:0 -s:v:1 1280x720 -b:v:1 2800k -maxrate:v:1 2996k -bufsize:v:1 4200k "
            + "-map 0:v:0 -s:v:2 854x480 -b:v:2 1400k -maxrate:v:2 1498k -bufsize:v:2 2100k "
            + "-map 0:v:0 -s:v:3 640x360 -b:v:3 800k -maxrate:v:3 856k -bufsize:v:3 1200k "
            + "-f hls -hls_time 10 -hls_playlist_type vod -hls_segment_filename \"%s/v%%v_segment_%%03d.ts\" "
            + "-var_stream_map \"v:0 v:1 v:2 v:3\" -master_pl_name \"%s\" \"%s/v%%v.m3u8\"";

    public static final String AUDIO_COMMAND = "ffmpeg -i \"%s\" -vn -acodec aac -ar 44100 -ac 2 "
            + "-map 0:a:0 -b:a:0 192k "
            + "-map 0:a:0 -b:a:1 128k "
            + "-map 0:a:0 -b:a:2 64k "
            + "-f hls -hls_time 10 -hls_playlist_type vod -hls_segment_filename \"%s/a%%v_segment_%%03d.ts\" "
            + "-var_stream_map \"a:0 a:1 a:2\" -master_pl_name \"%s\" \"%s/a%%v.m3u8\"";

    public static final String MASTER_PLAYLIST_NAME = "master.m3u8";
    public static final String PREVIEW_FILENAME = "preview.mp4";
    public static final String THUMBNAIL_FILENAME = "thumbnail.jpg";

}
