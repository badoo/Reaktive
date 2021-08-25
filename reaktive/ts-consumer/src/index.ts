import * as _reaktive from '@com.badoo/reaktive'

const reaktive = _reaktive.com.badoo.reaktive



async function main(): Promise<void> {
    console.log("START")
    console.log(await reaktive.reaktiveFun("simple"))
    console.log(await reaktive.reaktiveExtensionFun("receiver","extension"))
    console.log("END")
}

main().catch(console.error);