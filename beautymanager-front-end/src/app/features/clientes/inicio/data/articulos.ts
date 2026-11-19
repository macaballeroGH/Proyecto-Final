
export interface ArticuloInicio {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    icono: string;
    tiempoLectura: string;
    contenido: string[];
    fuenteNombre?: string;
    fuenteUrl?: string;
    imagen: string;
    textoAlternativo: string;
}

export const CATEGORIAS_ARTICULOS: string[] = [
    'Todos',
    'Cuidado de la piel',
    'Cabello',
    'Uñas',
    'Maquillaje',
    'Bienestar'
];


//========================
//Artículos de belleza
//========================

export const ARTICULOS_BELLEZA: ArticuloInicio[] = [

    {
        id: 1,
        titulo: 'Cómo organizar tu rutina facial',
        descripcion:
            'Descubrí cómo establecer una rutina de ' +
            'cuidado de la piel sencilla y práctica.',
        categoria: 'Cuidado de la piel',
        icono: 'face',
        tiempoLectura: '3 min',
        contenido: [
            'Una rutina facial no tiene que ser complicada. ' +
            'Lo importante es elegir productos adecuados ' +
            'para tu piel y utilizarlos de manera constante.',

            'Por la mañana, podés comenzar con una limpieza ' +
            'suave, continuar con una crema hidratante y ' +
            'utilizar protección solar cuando corresponda.',

            'Por la noche, retirate el maquillaje si lo ' +
            'utilizaste y limpiá la piel con suavidad. ' +
            'Después podés aplicar tu hidratante habitual.',

            'Si algún producto provoca irritación, ' +
            'suspendé su uso y consultá con una profesional ' +
            'de dermatología si los síntomas persisten.'
        ],
        fuenteNombre: 'American Academy of Dermatology',
        fuenteUrl:
            'https://www.aad.org/public/everyday-care',
        imagen:'https://images.unsplash.com/photo-1580870069867-74c57ee1bb07?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Productos para una rutina de cuidado facial con flores rosadas'
    },

    {
        id: 2,
        titulo: 'Protección solar: lo que necesitás saber',
        descripcion:
            'Conocé algunas recomendaciones importantes ' +
            'para proteger la piel de la radiación solar.',
        categoria: 'Cuidado de la piel',
        icono: 'wb_sunny',
        tiempoLectura: '3 min',
        contenido: [
            'La radiación ultravioleta puede afectar ' +
            'la piel incluso durante los días nublados.',

            'Para las actividades al aire libre, elegí ' +
            'protección solar de amplio espectro, ' +
            'con FPS 30 o superior y resistencia al agua.',

            'Recordá proteger también las zonas expuestas ' +
            'que suelen olvidarse, como las orejas y el cuello.',

            'Cuando permanecés al aire libre, normalmente ' +
            'se recomienda reaplicar el protector cada ' +
            'dos horas, y después de nadar o transpirar, ' +
            'siguiendo las indicaciones del envase.'
        ],
        fuenteNombre: 'American Academy of Dermatology',
        fuenteUrl:
            'https://www.aad.org/public/everyday-care/sun-protection',
        imagen: 'https://images.unsplash.com/photo-1711891391357-c5f903dc1ea1?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Protector solar sobre una toalla en la playa'
    },

    {
        id: 3,
        titulo: 'Pequeños hábitos para cuidar tu cabello',
        descripcion:
            'Consejos cotidianos para reducir el daño ' +
            'y mantener el cabello saludable.',
        categoria: 'Cabello',
        icono: 'air',
        tiempoLectura: '3 min',
        contenido: [
            'El cuidado del cabello comienza con hábitos ' +
            'que respeten sus características naturales.',

            'La frecuencia del lavado depende del cuero ' +
            'cabelludo, del tipo de cabello y de la ' +
            'acumulación de grasa o productos.',

            'Aplicá acondicionador según las necesidades ' +
            'de tu cabello y desenredalo con suavidad.',

            'Reducir el calor excesivo y evitar peinados ' +
            'demasiado tirantes también puede ayudar ' +
            'a disminuir el daño.'
        ],
        fuenteNombre: 'American Academy of Dermatology',
        fuenteUrl:
            'https://www.aad.org/public/everyday-care/hair-scalp-care/hair',
        imagen: 'https://images.unsplash.com/photo-1770576568718-6747e3d85de8?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Cabello largo y ondulado'
    },

    {
        id: 4,
        titulo: 'Uñas cuidadas más allá de la manicura',
        descripcion:
            'Aprendé hábitos sencillos para proteger ' +
            'las uñas y la piel que las rodea.',
        categoria: 'Uñas',
        icono: 'back_hand',
        tiempoLectura: '2 min',
        contenido: [
            'El cuidado de las uñas va más allá ' +
            'del esmalte o del diseño que elijas.',

            'Mantenelas limpias y secas para reducir ' +
            'el riesgo de problemas relacionados con ' +
            'la humedad.',

            'Al cortarlas, utilizá herramientas adecuadas ' +
            'y limá suavemente los bordes.',

            'Evitá cortar o empujar agresivamente ' +
            'las cutículas, porque ayudan a proteger ' +
            'la zona de crecimiento de la uña.'
        ],
        fuenteNombre: 'American Academy of Dermatology',
        fuenteUrl:
            'https://www.aad.org/public/everyday-care/nail-care-secrets/basics/healthy-nail-tips',
        imagen: 'https://images.unsplash.com/photo-1610992015732-2449b76344bc?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Manicura cuidada con esmalte rosa claro'
    },

    {
        id: 5,
        titulo: 'Un maquillaje sencillo para todos los días',
        descripcion:
            'Ideas para crear un maquillaje natural ' +
            'y adaptar los productos a tu estilo.',
        categoria: 'Maquillaje',
        icono: 'brush',
        tiempoLectura: '2 min',
        contenido: [
            'El maquillaje puede ser una forma de ' +
            'expresión personal. No existe una única ' +
            'manera correcta de utilizarlo.',

            'Podés comenzar con pocos productos: ' +
            'un toque de corrector, rubor, máscara ' +
            'de pestañas o un labial que te guste.',

            'Lo importante es elegir un resultado ' +
            'con el que te sientas cómoda.',

            'Cuando termines el día, retirate el ' +
            'maquillaje con suavidad y mantené ' +
            'limpias tus herramientas.'
        ],
        imagen: 'https://images.unsplash.com/photo-1764333746618-6285bf70db23?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Brochas y productos de maquillaje sobre una mesa'
    },

    {
        id: 6,
        titulo: 'El valor de una pausa de autocuidado',
        descripcion:
            'Ideas para incorporar pequeños momentos ' +
            'de bienestar a tu rutina.',
        categoria: 'Bienestar',
        icono: 'self_improvement',
        tiempoLectura: '2 min',
        contenido: [
            'El autocuidado no necesita convertirse ' +
            'en otra obligación de tu día.',

            'Podés reservar unos minutos para ' +
            'escuchar música, leer, disfrutar ' +
            'de una ducha tranquila o descansar.',

            'También podés aprovechar ese momento ' +
            'para realizar una rutina sencilla ' +
            'de cuidado de la piel o del cabello.',

            'Elegí actividades que realmente disfrutes ' +
            'y adaptalas a tus tiempos y preferencias.'
        ],
        imagen: 'https://images.unsplash.com/photo-1620733723572-11c53f73a416?auto=format&fit=crop&w=1000&q=85',
        textoAlternativo: 'Velas, toallas y difusor en un ambiente de relajación'
    }
];