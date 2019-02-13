package glimpse.glide

import android.graphics.Bitmap
import android.os.Build.ID
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import glimpse.core.crop
import glimpse.core.findCenter
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.MessageDigest

class GlimpseTransformation(private val optimizeZoom: Boolean) : BitmapTransformation() {

    companion object {
        private val id = "glimpse.glide.transformation"
        private val idBytes = id.toByteArray(Charset.forName("UTF-8"))
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        if (toTransform.width == outWidth || toTransform.height == outHeight
            || outWidth == 0 || outWidth == 1
            || outHeight == 0 || outHeight == 1
        ) {
            return toTransform
        }

        val (x, y) = toTransform.findCenter()
        return toTransform.crop(x, y, outWidth, outHeight, optimizeZoom = optimizeZoom)
    }

    override fun equals(other: Any?): Boolean = if (other is GlimpseTransformation) {
        optimizeZoom == other.optimizeZoom
    } else {
        false
    }

    override fun hashCode(): Int = Util.hashCode(ID.hashCode(), Util.hashCode(optimizeZoom))

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
        messageDigest.update(ByteBuffer.allocate(4).putInt(if (optimizeZoom) 1 else 0).array())
    }
}