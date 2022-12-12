package com.communi.suggestu.scena.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransformationUtils
{

    private TransformationUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: TransformationHelper. This is a utility class");
    }

    public static Quaternionf quatFromXYZ(Vector3f xyz, boolean degrees)
    {
        return quatFromXYZ(xyz.x, xyz.y, xyz.z, degrees);
    }

    public static Quaternionf quatFromXYZ(float[] xyz, boolean degrees)
    {
        return quatFromXYZ(xyz[0], xyz[1], xyz[2], degrees);
    }

    public static Quaternionf quatFromXYZ(float x, float y, float z, boolean degrees)
    {
        float conversionFactor = degrees ? (float) Math.PI / 180 : 1;
        return new Quaternionf().rotationXYZ(x * conversionFactor, y * conversionFactor, z * conversionFactor);
    }

    public static Quaternionf makeQuaternion(float[] values)
    {
        return new Quaternionf(values[0], values[1], values[2], values[3]);
    }

    public static class Deserializer implements JsonDeserializer<Transformation>
    {
        @Override
        public Transformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return Transformation.identity();
                }
                else
                {
                    throw new JsonParseException("TRSR: unknown default string: " + transform);
                }
            }
            if (json.isJsonArray())
            {
                // direct matrix array
                return new Transformation(parseMatrix(json));
            }
            if (!json.isJsonObject()) throw new JsonParseException("TRSR: expected array or object, got: " + json);
            JsonObject obj = json.getAsJsonObject();
            Transformation ret;
            if (obj.has("matrix"))
            {
                // matrix as a sole key
                ret = new Transformation(parseMatrix(obj.get("matrix")));
                if (obj.entrySet().size() > 1)
                {
                    throw new JsonParseException("TRSR: can't combine matrix and other keys");
                }
                return ret;
            }
            Vector3f translation = null;
            Quaternionf leftRot = null;
            Vector3f scale = null;
            Quaternionf rightRot = null;
            Set<String> elements = new HashSet<>(obj.keySet());
            if (obj.has("translation"))
            {
                translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                elements.remove("translation");
            }
            if (obj.has("rotation"))
            {
                leftRot = parseRotation(obj.get("rotation"));
                elements.remove("rotation");
            }
            else if (obj.has("left_rotation"))
            {
                leftRot = parseRotation(obj.get("left_rotation"));
                elements.remove("left_rotation");
            }
            if (obj.has("scale"))
            {
                if(!obj.get("scale").isJsonArray())
                {
                    try
                    {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + obj.get("scale"));
                    }
                }
                else
                {
                    scale = new Vector3f(parseFloatArray(obj.get("scale"), 3, "Scale"));
                }
                elements.remove("scale");
            }
            if (obj.has("right_rotation"))
            {
                rightRot = parseRotation(obj.get("right_rotation"));
                elements.remove("right_rotation");
            }
            else if (obj.has("post-rotation"))
            {
                rightRot = parseRotation(obj.get("post-rotation"));
                elements.remove("post-rotation");
            }
            if (!elements.isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation' OR 'left_rotation', 'scale', 'post-rotation' (legacy) OR 'right_rotation', 'origin'. Found: " + String.join(", ", elements));

            return new Transformation(translation, leftRot, scale, rightRot);
        }

        public static Matrix4f parseMatrix(JsonElement e)
        {
            if (!e.isJsonArray()) throw new JsonParseException("Matrix: expected an array, got: " + e);
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            Matrix4f matrix = new Matrix4f().zero();
            for (int rowIdx = 0; rowIdx < 3; rowIdx++)
            {
                if (!m.get(rowIdx).isJsonArray()) throw new JsonParseException("Matrix row: expected an array, got: " + m.get(rowIdx));
                JsonArray r = m.get(rowIdx).getAsJsonArray();
                if (r.size() != 4) throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                for (int columnIdx = 0; columnIdx < 4; columnIdx++)
                {
                    try
                    {
                        matrix.set(columnIdx, rowIdx, r.get(columnIdx).getAsNumber().floatValue());
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(columnIdx));
                    }
                }
            }
            return matrix;
        }

        public static float[] parseFloatArray(JsonElement e, int length, String prefix)
        {
            if (!e.isJsonArray()) throw new JsonParseException(prefix + ": expected an array, got: " + e);
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            float[] ret = new float[length];
            for (int i = 0; i < length; i++)
            {
                try
                {
                    ret[i] = t.get(i).getAsNumber().floatValue();
                }
                catch (ClassCastException ex)
                {
                    throw new JsonParseException(prefix + " element: expected number, got: " + t.get(i));
                }
            }
            return ret;
        }

        public static Quaternionf parseAxisRotation(JsonElement e)
        {
            if (!e.isJsonObject()) throw new JsonParseException("Axis rotation: object expected, got: " + e);
            JsonObject obj  = e.getAsJsonObject();
            if (obj.entrySet().size() != 1) throw new JsonParseException("Axis rotation: expected single axis object, got: " + e);
            Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
            Quaternionf ret;
            try
            {
                ret = switch (entry.getKey()) {
                    case "x" -> Axis.XP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
                    case "y" -> Axis.YP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
                    case "z" -> Axis.ZP.rotationDegrees(entry.getValue().getAsNumber().floatValue());
                    default ->
                            throw new JsonParseException("Axis rotation: expected single axis key, got: " + entry.getKey());
                };
            }
            catch(ClassCastException ex)
            {
                throw new JsonParseException("Axis rotation value: expected number, got: " + entry.getValue());
            }
            return ret;
        }

        public static Quaternionf parseRotation(JsonElement e)
        {
            if (e.isJsonArray())
            {
                if (e.getAsJsonArray().get(0).isJsonObject())
                {
                    Quaternionf ret = new Quaternionf();
                    for (JsonElement a : e.getAsJsonArray())
                    {
                        ret.mul(parseAxisRotation(a));
                    }
                    return ret;
                }
                else if (e.isJsonArray())
                {
                    JsonArray array = e.getAsJsonArray();
                    if (array.size() == 3) //Vanilla rotation
                        return quatFromXYZ(parseFloatArray(e, 3, "Rotation"), true);
                    else // quaternion
                        return makeQuaternion(parseFloatArray(e, 4, "Rotation"));
                }
                else throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
            else if (e.isJsonObject())
            {
                return parseAxisRotation(e);
            }
            else throw new JsonParseException("Rotation: expected array or object, got: " + e);
        }
    }
}
