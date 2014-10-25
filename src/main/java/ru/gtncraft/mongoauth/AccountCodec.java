package ru.gtncraft.mongoauth;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class AccountCodec implements CollectibleCodec<Account> {
    @Override
    public boolean documentHasId(final Account value) {
        return false;
    }

    @Override
    public BsonObjectId getDocumentId(final Account value) {
        return null;
    }

    @Override
    public void generateIdIfAbsentFromDocument(final Account value) {
    }

    @Override
    public void encode(final BsonWriter writer, final Account value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("uuid", value.getId().toString());
        writer.writeInt64("ip", value.getIp());
        writer.writeBoolean("allowed", value.isAllowed());
        writer.writeString("password", value.getPassword());
        writer.writeEndDocument();
    }

    @Override
    public Account decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        UUID id = UUID.fromString(reader.readString("uuid"));
        long ip = reader.readInt64("ip");
        boolean allowed = reader.readBoolean("allowed");
        String password = reader.readString("password");
        reader.readEndDocument();
        return new Account(id, ip, password, allowed);
    }

    @Override
    public Class<Account> getEncoderClass() {
        return Account.class;
    }
}
