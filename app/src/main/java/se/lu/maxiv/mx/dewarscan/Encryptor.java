package se.lu.maxiv.mx.dewarscan;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import static se.lu.maxiv.mx.dewarscan.LogTag.TAG;

public class Encryptor
{
    static final String KEYSTORE = "AndroidKeyStore";
    static final String KEY_ALIAS = "dewar_scanner";
    static final String TYPE_RSA = "RSA";
    static final String CYPHER = "RSA/ECB/PKCS1Padding";
    static final String ENCODING = "UTF-8";

    Context context;

    public Encryptor(Context context)
    {
        this.context = context;
    }

    void createKeys() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException
    {
        /* validity range of the key pair that's about to be generated */
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 25);

        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                /* you'll use the alias later to retrieve the key */
                .setAlias(KEY_ALIAS)
                /* the subject used for the self-signed certificate of the generated pair */
                .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                /* the serial number used for the self-signed certificate of the */
                .setSerialNumber(BigInteger.valueOf(1337))
                /* date range of validity for the generated pair */
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        /* initialize a KeyPair generator using the RSA algorithm and the KeyStore */
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, KEYSTORE);
        kpGenerator.initialize(spec);

        final KeyPair kp = kpGenerator.generateKeyPair();
        Log.i(TAG, "Public Key is: " + kp.getPublic().toString());
    }

    KeyStore.PrivateKeyEntry getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableEntryException
    {
        KeyStore ks = KeyStore.getInstance(KEYSTORE);

        ks.load(null);

        /* load the key pair from the Android Key Store */
        KeyStore.Entry entry = ks.getEntry(KEY_ALIAS, null);

        /* if the entry is null, keys were never stored under this alias */
        if (entry == null)
        {
            Log.w(TAG, "No key found under alias: " + KEY_ALIAS);
            Log.w(TAG, "Generating new key...");
            try
            {
                createKeys();

                /* reload keystore */
                ks = KeyStore.getInstance(KEYSTORE);
                ks.load(null);

                /* reload key pair */
                entry = ks.getEntry(KEY_ALIAS, null);

                if (entry == null)
                {
                    Log.w(TAG, "Generating new key failed...");
                    return null;
                }
            }
            catch (NoSuchProviderException | InvalidAlgorithmParameterException e)
            {
                Log.w(TAG, "Generating new key failed...");
                Log.e(TAG, e.toString());
                return null;
            }
        }

        if (!(entry instanceof KeyStore.PrivateKeyEntry))
        {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            return null;
        }

        return (KeyStore.PrivateKeyEntry) entry;

    }

    public String encrypt(String toEncrypt)
    {
        try
        {
            final KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey();
            if (privateKeyEntry != null)
            {
                final PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();
                Cipher input = Cipher.getInstance(CYPHER);
                input.init(Cipher.ENCRYPT_MODE, publicKey);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
                cipherOutputStream.write(toEncrypt.getBytes(ENCODING));
                cipherOutputStream.close();
                byte[] vals = outputStream.toByteArray();
                return Base64.encodeToString(vals, Base64.DEFAULT);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "error encrypting " + e);
            return null;
        }
        return null;
    }

    public String decrypt(String encrypted)
    {
        try
        {
            KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey();
            if (privateKeyEntry != null)
            {
                final PrivateKey privateKey = privateKeyEntry.getPrivateKey();

                Cipher output = Cipher.getInstance(CYPHER);
                output.init(Cipher.DECRYPT_MODE, privateKey);

                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), output);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1)
                {
                    values.add((byte) nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++)
                {
                    bytes[i] = values.get(i);
                }

                return new String(bytes, 0, bytes.length, ENCODING);
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, "error decrypting" + e);
            return null;
        }

        return null;
    }
}
