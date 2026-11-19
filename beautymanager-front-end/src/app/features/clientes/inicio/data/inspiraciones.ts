export interface InspiracionBelleza {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    icono: string;
    estilo: string;

    imagen: string;
    textoAlternativo: string;
    fotografo: string;
    enlaceFoto: string;
}


//========================
//Inspiraciones de belleza
//========================

export const INSPIRACIONES_BELLEZA: InspiracionBelleza[] = [

    {
        id: 1,
        titulo: 'Belleza natural',
        descripcion:
            'Inspirate en un maquillaje delicado, ' +
            'con tonos suaves y un acabado fresco.',
        categoria: 'Maquillaje',
        icono: 'face',
        estilo: 'natural',

        imagen:
            'https://images.unsplash.com/' +
            'photo-1747822932222-d71669b652c6' +
            '?auto=format&fit=crop&w=900&q=85',

        textoAlternativo:
            'Retrato de belleza natural con maquillaje suave',

        fotografo: 'Zulfugar Karimov',

        enlaceFoto:
            'https://unsplash.com/photos/' +
            'young-woman-peeking-through-green-willow-branches-HzRb9DH7OtY'
    },

    {
        id: 2,
        titulo: 'Manicura elegante',
        descripcion:
            'Uñas en tonos rosados y nude, ' +
            'ideales para un estilo delicado y minimalista.',
        categoria: 'Uñas',
        icono: 'back_hand',
        estilo: 'manicura',

        imagen:
            'https://images.unsplash.com/' +
            'photo-1610992015762-45dca7fa3a85' +
            '?auto=format&fit=crop&w=900&q=85',

        textoAlternativo:
            'Manicura elegante con esmalte rosa claro',

        fotografo: 'Chelson Tamares',

        enlaceFoto:
            'https://unsplash.com/photos/' +
            'hand-with-light-pink-manicured-nails-vtQHwU4F13s'
    },

    {
        id: 3,
        titulo: 'Ondas y movimiento',
        descripcion:
            'Un peinado de ondas suaves con ' +
            'movimiento natural y tonos combinados.',
        categoria: 'Cabello',
        icono: 'auto_awesome',
        estilo: 'cabello',

        imagen:
            'https://images.unsplash.com/' +
            'photo-1785456411888-c2e842552820' +
            '?auto=format&fit=crop&w=900&q=85',

        textoAlternativo:
            'Cabello largo con ondas suaves y tonos degradados',

        fotografo: 'Yovanka Loria Salon',

        enlaceFoto:
            'https://unsplash.com/photos/' +
            'persons-hand-on-long-wavy-ombre-hair-e900d39_6No'
    },

    {
        id: 4,
        titulo: 'Un momento de spa',
        descripcion:
            'Un ambiente tranquilo con velas, ' +
            'toallas y detalles para disfrutar del bienestar.',
        categoria: 'Bienestar',
        icono: 'spa',
        estilo: 'bienestar',

        imagen:
            'https://images.unsplash.com/' +
            'photo-1620733723572-11c53f73a416' +
            '?auto=format&fit=crop&w=900&q=85',

        textoAlternativo:
            'Ambiente de spa con toallas y velas decorativas',

        fotografo: 'Mediamodifier',

        enlaceFoto:
            'https://unsplash.com/photos/' +
            'reed-diffuser-with-candles-and-towel-W7AyAs7azHc'
    }
];
