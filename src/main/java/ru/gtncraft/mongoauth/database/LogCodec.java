package ru.gtncraft.mongoauth.database;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.UUID;

class LogCodec implements CollectibleCodec<Log> {
    @Override
    public boolean documentHasId(final Log value) {
        return true;
    }

    @Override
    public BsonObjectId getDocumentId(final Log value) {
        return new BsonObjectId(value.getId());
    }

    @Override
    public void generateIdIfAbsentFromDocument(final Log value) {
    }

    @Override
    public void encode(final BsonWriter writer, final Log value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("id", value.getId());
        writer.writeString("uuid", value.getPlayer().toString());
        writer.writeInt64("ip", value.getIp());
        writer.writeInt32("status", value.getStatus().getIntRepresentation());
        writer.writeEndDocument();
    }

    @Override
    public Log decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("id");
        UUID uuid = UUID.fromString(reader.readString("uuid"));
        long ip = reader.readInt64();
        Log.Status status = Log.Status.fromInt(reader.readInt32("status"));
        reader.readEndDocument();
        return new Log(id, uuid, ip, status);
    }

    @Override
    public Class<Log> getEncoderClass() {
        return Log.class;
    }
}
