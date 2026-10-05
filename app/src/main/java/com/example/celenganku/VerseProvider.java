package com.example.celenganku;

import java.util.Random;

public class VerseProvider {

    private static final String[] AYAT_MOTIVASI = {
            "\"Setiap orang hendaklah memberi menurut kerelaan hatinya, jangan dengan sedih hati atau karena paksaan, sebab Allah mengasihi orang yang memberi dengan sukacita.\" (2 Korintus 9:7)",
            "\"Muliakanlah TUHAN dengan hartamu dan dengan hasil pertama dari segala penghasilanmu.\" (Amsal 3:9)",
            "\"Berilah dan kamu akan diberi: suatu takaran yang baik, yang dipadatkan, yang digoncang dan yang tumpah keluar akan dicurahkan ke dalam pangkuanmu.\" (Lukas 6:38)",
            "\"Sebab di mana hartamu berada, di situ juga hatimu berada.\" (Matius 6:21)",
            "\"Orang yang baik hati akan diberkati, karena ia membagi rezekinya dengan orang miskin.\" (Amsal 22:9)",
            "\"Hai pemalas, belajarlah dari semut, perhatikanlah lakunya dan jadilah bijak! Ia menyediakan rotinya di musim panas, dan mengumpulkan makanannya pada waktu panen.\" (Amsal 6:6-8)",
            "\"Ada yang menyebar harta, tetapi bertambah kaya, ada yang menghemat secara luar biasa, namun selalu berkekurangan.\" (Amsal 11:24)",
            "\"Hendaklah kamu murah hati, sama seperti Bapamu adalah murah hati.\" (Lukas 6:36)",
            "\"Lebih berbahagia memberi daripada menerima.\" (Kisah Para Rasul 20:35)",
            "\"Siapa menaruh belas kasihan kepada orang yang lemah, memutangi TUHAN, yang akan membalas perbuatannya itu.\" (Amsal 19:17)",
            "\"Harta yang diperoleh dengan cepat akan berkurang, tetapi siapa mengumpulkan sedikit demi sedikit, menjadi kaya.\" (Amsal 13:11)",
            "\"Rancangan orang rajin mendatangkan kelimpahan, tetapi setiap orang yang tergesa-gesa hanya mendatangkan kekurangan.\" (Amsal 21:5)",
            "\"Percayalah kepada TUHAN dengan segenap hatimu, dan janganlah bersandar kepada pengertianmu sendiri.\" (Amsal 3:5)",
            "\"Segala perkara dapat kutanggung di dalam Dia yang memberi kekuatan kepadaku.\" (Filipi 4:13)",
            "\"Allahku akan memenuhi segala keperluanmu menurut kekayaan dan kemuliaan-Nya dalam Kristus Yesus.\" (Filipi 4:19)",
            "\"Janganlah kamu menjadi hamba uang dan cukuplah dirimu dengan apa yang ada padamu. Karena Allah telah berfirman: 'Aku sekali-kali tidak akan membiarkan engkau dan Aku sekali-kali tidak akan meninggalkan engkau.'\" (Ibrani 13:5)",
            "\"Siapa setia dalam perkara kecil, ia setia juga dalam perkara besar.\" (Lukas 16:10)",
            "\"Sebab siapakah di antara kamu yang kalau mau mendirikan menara tidak duduk membuat anggaran biaya dahulu, kalau-kalau cukup uangnya untuk menyelesaikan pekerjaan itu?\" (Lukas 14:28)",
            "\"Kekayaan dan kehormatan berasal dari pada--Mu dan Engkaulah yang berkuasa atas segalanya.\" (1 Tawarikh 29:12)",
            "\"Pikirkanlah perkara yang di atas, bukan yang di bumi.\" (Kolose 3:2)",
            "\"Kasihilah sesamamu manusia seperti dirimu sendiri.\" (Matius 22:39)",
            "\"TUHAN adalah gembalaku, takkan kekurangan aku.\" (Mazmur 23:1)",
            "\"Serahkanlah perbuatanmu kepada TUHAN, maka terlaksanalah segala rencanamu.\" (Amsal 16:3)",
            "\"Berkat Tuhanlah yang menjadikan kaya, susah payah tidak akan menambahkannya.\" (Amsal 10:22)",
            "\"Orang bijak menyimpan harta yang indah, tetapi orang bodoh menghabiskannya.\" (Amsal 21:20)"
    };

    private static int currentIndex = 0;

    public static String getVerseAuto() {
        Random random = new Random();
        currentIndex = random.nextInt(AYAT_MOTIVASI.length);
        return AYAT_MOTIVASI[currentIndex];
    }

    public static String getNextVerse() {
        currentIndex = (currentIndex + 1) % AYAT_MOTIVASI.length;
        return AYAT_MOTIVASI[currentIndex];
    }
}