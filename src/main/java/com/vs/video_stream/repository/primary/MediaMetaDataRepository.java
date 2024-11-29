package com.vs.video_stream.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vs.video_stream.model.MediaMetaData;

public interface MediaMetaDataRepository extends MongoRepository<MediaMetaData, String> {

}