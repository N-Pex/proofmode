package com.eternitywall.ots.op;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import com.eternitywall.ots.StreamDeserializationContext;


/**
 * Cryptographic transformations.
 * These transformations have the unique property that for any length message,
 * the size of the result they return is fixed. Additionally, they're the only
 * type of operation that can be applied directly to a stream.
 *
 * @see com.eternitywall.ots.op.OpUnary
 */
public class OpCrypto extends OpUnary {

    private static Logger log = Logger.getLogger(OpCrypto.class.getName());

    public String _TAG_NAME = "";

    public String _HASHLIB_NAME() {
        return "";
    }

    public int _DIGEST_LENGTH() {
        return 0;
    }

    OpCrypto() {
        super();
    }

    public static Op deserializeFromTag(StreamDeserializationContext ctx, byte tag) {
        return OpUnary.deserializeFromTag(ctx, tag);
    }

    @Override
    public byte[] call(byte[] msg) {
        // For Sha1 & Sha256 use java.security.MessageDigest library
        try {
            MessageDigest digest = MessageDigest.getInstance(this._HASHLIB_NAME());
            byte[] hash = digest.digest(msg);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            log.severe("NoSuchAlgorithmException");
            e.printStackTrace();
            return new byte[]{};
        }
    }

    public byte[] hashFd(StreamDeserializationContext ctx) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance(this._HASHLIB_NAME());
            byte[] chunk = ctx.read(1048576);
            while (chunk != null && chunk.length > 0) {
                digest.update(chunk);
                chunk = ctx.read(1048576);
            }
            byte[] hash = digest.digest();
            return hash;
    }

    public byte[] hashFd(File file) throws IOException, NoSuchAlgorithmException {
        return hashFd(new FileInputStream(file));
    }


    public byte[] hashFd(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(this._HASHLIB_NAME());
        byte[] chunk = new byte[1048576];
        int count = inputStream.read(chunk, 0, 1048576);
        while (count >= 0) {
            digest.update(chunk,0,count);
            count = inputStream.read(chunk, 0, 1048576);
        }
        inputStream.close();
        byte[] hash = digest.digest();
        return hash;
    }


}