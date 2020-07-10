package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.samplemppmodule.store.KittenLoader
import com.badoo.reaktive.samplemppmodule.store.KittenLoader.Result
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturnValue
import com.badoo.reaktive.single.single
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLResponse
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataTaskWithURL
import kotlin.native.concurrent.freeze

internal class KittenLoaderImpl : KittenLoader {

    override fun load(): Single<Result> =
        single<String> { emitter ->
            val callback: (NSData?, NSURLResponse?, NSError?) -> Unit =
                { data: NSData?, _, error: NSError? ->
                    if (data != null) {
                        emitter.onSuccess(NSString.create(data, NSUTF8StringEncoding).toString())
                    } else {
                        emitter.onError(Exception(error?.debugDescription ?: "Error loading kitten"))
                    }
                }

            val task = NSURLSession.sharedSession.dataTaskWithURL(NSURL(string = Config.KITTEN_URL), callback.freeze())
            task.resume()
            emitter.setCancellable(task::cancel)
        }
            .map(Result::Success)
            .onErrorReturnValue(Result.Error)
}
