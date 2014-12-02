package ru.gtncraft.mongoauth.database;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

class AccountCodec implements CollectibleCodec<Account> {
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
        writer.writeString("login", value.getLogin().toLowerCase());
        writer.writeInt64("ip", value.getIp());
        writer.writeString("password", value.getPassword());
        writer.writeEndDocument();
    }

    @Override
    public Account decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        String login = reader.readString("login");
        long ip = reader.readInt64("ip");
        String password = reader.readString("password");
        reader.readEndDocument();
        return new Account(login, ip, password);
    }

    @Override
    public Class<Account> getEncoderClass() {
        return Account.class;
    }
}
