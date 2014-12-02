package ru.gtncraft.mongoauth.database;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

class AccountCodec implements CollectibleCodec<Account> {
    @Override
    public boolean documentHasId(final Account value) {
        return true;
    }

    @Override
    public BsonObjectId getDocumentId(final Account value) {
        return new BsonObjectId(value.getId());
    }

    @Override
    public void generateIdIfAbsentFromDocument(final Account value) {
    }

    @Override
    public void encode(final BsonWriter writer, final Account value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", value.getId());
        writer.writeInt64("ip", value.getIp());
        writer.writeString("password", value.getPassword());
        writer.writeString("login", value.getLogin());
        writer.writeEndDocument();
    }

    @Override
    public Account decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        long ip = reader.readInt64("ip");
        String password = reader.readString("password");
        String login = reader.readString("login");
        reader.readEndDocument();
        return new Account(id, login, ip, password);
    }

    @Override
    public Class<Account> getEncoderClass() {
        return Account.class;
    }
}
