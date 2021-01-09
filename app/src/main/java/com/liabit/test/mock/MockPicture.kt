package com.sport.day.net

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/11/6 10:53
 * 用于测试的图片
 */
object MockPicture {
//    private const val p0 = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1441858807,2156496567&fm=15&gp=0.jpg"
//    private const val p1 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132958926&di=ec35eb25a1c567e0c1db6e9efc26ff16&imgtype=0&src=http%3A%2F%2Fimg.gaokaozy.cn%2Fschool%2Fphoto%2F3%2F58d7212486502.jpg"
//    private const val p2 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937899&di=a3fa49d37dcfb8b06152d2854371ad24&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fitbbs%2F1404%2F05%2Fc8%2F32827219_1396701285552.jpg"
//    private const val p3 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937892&di=8ec2b9bfd63f59cd67c249203c6813a8&imgtype=0&src=http%3A%2F%2Fwww.isixue.com%2Fuploads%2F20181023%2Fdedecbbffc13fd2178ccbf0983790b18.jpg"
//    private const val p4 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937892&di=f67394a74bcec66948b17556a50e0506&imgtype=0&src=http%3A%2F%2Fupload.univs.cn%2F2012%2F0924%2Fthumb_940__1348480595945.JPG"
//    private const val p5 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937890&di=755cc6a52ad45dc024c48d1ed9d3d58d&imgtype=0&src=http%3A%2F%2Fpic110.nipic.com%2Ffile%2F20160925%2F1389834_181220800822_2.jpg"
//    private const val p6 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937934&di=61ceb3965131fa7cb80fd07f825694cf&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsx%2F36_img%2Fupload%2F750ae4b5%2F20170818%2FOskP-fykcpsc5365411.jpg"
//    private const val p7 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937934&di=657137b76575fcec6bacdb5c597db19d&imgtype=0&src=http%3A%2F%2Fupload.univs.cn%2F2012%2F1101%2Fthumb_940__1351757407155.jpg"
//    private const val p8 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193596&di=f573f86cac694444bfff0fc10f03ceb5&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1510%2F15%2Fc20%2F14007235_1444914757066_mthumb.jpg"
//    private const val p9 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193595&di=c861dcf2dbeafb999f539e5d745f9429&imgtype=0&src=http%3A%2F%2Fjjc.xxu.edu.cn%2F_mediafile%2Fjjc%2F2012%2F12%2F18%2F101h08ntdr.jpg"
//    private const val p10 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193593&di=d0feba1780e6cd77a46ebac0208f3530&imgtype=0&src=http%3A%2F%2Fimg.yanj.cn%2Feditor%2F201606%2F20160630163542_56121.jpg"
//    private const val p11 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193590&di=fc108c6a650a7e17eaf636bb44841bac&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fc0%253Dbaike60%252C5%252C5%252C60%252C20%2Fsign%3D28f7b0e2cebf6c81e33a24badd57da50%2Fa08b87d6277f9e2fb37d49821d30e924b999f3f4.jpg"
//    private const val p12 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193588&di=85ec478d1795443df7639bdb8775afa6&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fbaike%2Fc0%3Dbaike60%2C5%2C5%2C60%2C20%2Fsign%3D2ff7683779f40ad101e9cfb136457aba%2F9c16fdfaaf51f3de9103e66b96eef01f3a29790f.jpg"
//    private const val p13 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193588&di=474678c5b528c190c27d7242bcb114fc&imgtype=0&src=http%3A%2F%2Fpic39.nipic.com%2F20140310%2F13456689_133016284000_2.jpg"
//    private const val p14 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193587&di=793a81c0cb904264beb4b3982e6b4144&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1510%2F24%2Fc16%2F14411982_14411982_1445699942261.jpg"
//    private const val p15 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246498&di=b3ef095bf1fe1fa3f496d69d6d3ec308&imgtype=0&src=http%3A%2F%2Fimg8.zol.com.cn%2Fbbs%2Fupload%2F19930%2F19929487.JPG"
//    private const val p16 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246497&di=4b714f187a674ca3be1d5df25cbda5cd&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160926%2Fe1d592894f4849349e834cc9e8446efa_th.jpg"
//    private const val p17 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246497&di=ad21817c515e19f4981bbfa8a798f57f&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1308%2F26%2Fc8%2F24917251_24917251_1377506444187.jpg"
//    private const val p18 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133321457&di=d1b874773362a0477c4c40e3478aa6b1&imgtype=0&src=http%3A%2F%2Fstatic.gk66.cn%2FUploadFiles%2Fjpg%2F2008%2F12%2F20081201092954531.jpg"
//    private const val p19 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133321456&di=1488e1794471c6ba6941f45f0e41d077&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1512%2F27%2Fc8%2F16936814_1451208678624_mthumb.jpg"

    private const val p0 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fmmbiz.qpic.cn%2Fmmbiz_jpg%2F5Sgw1ho9Pc986n5UicrTd1Z19Nk0ay1HX57iaclGgx2vNqxQWpK1ujCHwoJdRFhAGRn7Y39qIhOAmcVw59Y4641A%2F640%3Fwx_fmt%3Djpeg&refer=http%3A%2F%2Fmmbiz.qpic.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612348603&t=75929a2bfddf616785c27bcb5a493a14"
    private const val p1 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132958926&di=ec35eb25a1c567e0c1db6e9efc26ff16&imgtype=0&src=http%3A%2F%2Fimg.gaokaozy.cn%2Fschool%2Fphoto%2F3%2F58d7212486502.jpg"
    private const val p2 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbbs.jooyoo.net%2Fattachment%2FMon_1204%2F27_498206_d1356d0ba2ea535.jpg&refer=http%3A%2F%2Fbbs.jooyoo.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612348603&t=17614c8d33a879f795d1a7e281af94d1"
    private const val p3 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937892&di=8ec2b9bfd63f59cd67c249203c6813a8&imgtype=0&src=http%3A%2F%2Fwww.isixue.com%2Fuploads%2F20181023%2Fdedecbbffc13fd2178ccbf0983790b18.jpg"
    private const val p4 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937892&di=f67394a74bcec66948b17556a50e0506&imgtype=0&src=http%3A%2F%2Fupload.univs.cn%2F2012%2F0924%2Fthumb_940__1348480595945.JPG"
    private const val p5 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937890&di=755cc6a52ad45dc024c48d1ed9d3d58d&imgtype=0&src=http%3A%2F%2Fpic110.nipic.com%2Ffile%2F20160925%2F1389834_181220800822_2.jpg"
    private const val p6 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937934&di=61ceb3965131fa7cb80fd07f825694cf&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsx%2F36_img%2Fupload%2F750ae4b5%2F20170818%2FOskP-fykcpsc5365411.jpg"
    private const val p7 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608132937934&di=657137b76575fcec6bacdb5c597db19d&imgtype=0&src=http%3A%2F%2Fupload.univs.cn%2F2012%2F1101%2Fthumb_940__1351757407155.jpg"
    private const val p8 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193596&di=f573f86cac694444bfff0fc10f03ceb5&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1510%2F15%2Fc20%2F14007235_1444914757066_mthumb.jpg"
    private const val p9 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193595&di=c861dcf2dbeafb999f539e5d745f9429&imgtype=0&src=http%3A%2F%2Fjjc.xxu.edu.cn%2F_mediafile%2Fjjc%2F2012%2F12%2F18%2F101h08ntdr.jpg"
    private const val p10 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193593&di=d0feba1780e6cd77a46ebac0208f3530&imgtype=0&src=http%3A%2F%2Fimg.yanj.cn%2Feditor%2F201606%2F20160630163542_56121.jpg"
    private const val p11 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193590&di=fc108c6a650a7e17eaf636bb44841bac&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fc0%253Dbaike60%252C5%252C5%252C60%252C20%2Fsign%3D28f7b0e2cebf6c81e33a24badd57da50%2Fa08b87d6277f9e2fb37d49821d30e924b999f3f4.jpg"
    private const val p12 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193588&di=85ec478d1795443df7639bdb8775afa6&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fbaike%2Fc0%3Dbaike60%2C5%2C5%2C60%2C20%2Fsign%3D2ff7683779f40ad101e9cfb136457aba%2F9c16fdfaaf51f3de9103e66b96eef01f3a29790f.jpg"
    private const val p13 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193588&di=474678c5b528c190c27d7242bcb114fc&imgtype=0&src=http%3A%2F%2Fpic39.nipic.com%2F20140310%2F13456689_133016284000_2.jpg"
    private const val p14 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133193587&di=793a81c0cb904264beb4b3982e6b4144&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1510%2F24%2Fc16%2F14411982_14411982_1445699942261.jpg"
    private const val p15 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246498&di=b3ef095bf1fe1fa3f496d69d6d3ec308&imgtype=0&src=http%3A%2F%2Fimg8.zol.com.cn%2Fbbs%2Fupload%2F19930%2F19929487.JPG"
    private const val p16 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246497&di=4b714f187a674ca3be1d5df25cbda5cd&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160926%2Fe1d592894f4849349e834cc9e8446efa_th.jpg"
    private const val p17 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133246497&di=ad21817c515e19f4981bbfa8a798f57f&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1308%2F26%2Fc8%2F24917251_24917251_1377506444187.jpg"
    private const val p18 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133321457&di=d1b874773362a0477c4c40e3478aa6b1&imgtype=0&src=http%3A%2F%2Fstatic.gk66.cn%2FUploadFiles%2Fjpg%2F2008%2F12%2F20081201092954531.jpg"
    private const val p19 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608133321456&di=1488e1794471c6ba6941f45f0e41d077&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1512%2F27%2Fc8%2F16936814_1451208678624_mthumb.jpg"


    private val pArray = arrayOf(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10,
            p11, p12, p13, p14, p15, p16, p17, p18, p19)

    private val random = Random()

    @JvmStatic
    fun random(): String {
        return pArray[random.nextInt(20)]
    }

    val size: Int = pArray.size

    @JvmStatic
    operator fun get(i: Int): String {
        return pArray[i % 20]
    }
}