export interface ConsejoProfesional {
    id: number;
    categoria: string;
    titulo: string;
    descripcion: string;
    icono: string;
}


//========================================
//CONSEJOS PROFESIONALES
//========================================

export const CONSEJOS_PROFESIONALES: ConsejoProfesional[] = [

    {
        id: 1,
        categoria: 'Organización',
        titulo: 'Prepará tu espacio antes de comenzar',
        descripcion:
            'Un espacio limpio, ordenado y con los materiales necesarios a mano permite trabajar con mayor tranquilidad y ofrecer una mejor experiencia.',
        icono: 'auto_awesome'
    },

    {
        id: 2,
        categoria: 'Atención al cliente',
        titulo: 'Escuchá antes de comenzar',
        descripcion:
            'Dedicar unos minutos a escuchar las expectativas y necesidades de cada clienta ayuda a brindar una atención más personalizada.',
        icono: 'forum'
    },

    {
        id: 3,
        categoria: 'Higiene',
        titulo: 'La higiene forma parte de cada atención',
        descripcion:
            'Revisá que el espacio, los instrumentos y los materiales cumplan los procedimientos de higiene correspondientes a cada servicio.',
        icono: 'clean_hands'
    },

    {
        id: 4,
        categoria: 'Bienestar profesional',
        titulo: 'Cuidá también tu bienestar',
        descripcion:
            'Aprovechá las pausas disponibles para descansar, hidratarte y mantener una postura cómoda durante tu jornada.',
        icono: 'self_improvement'
    },

    {
        id: 5,
        categoria: 'Comunicación',
        titulo: 'La claridad genera confianza',
        descripcion:
            'Explicar cómo se desarrollará una atención y responder las dudas de forma clara contribuye a una experiencia más agradable.',
        icono: 'record_voice_over'
    },

    {
        id: 6,
        categoria: 'Organización',
        titulo: 'Revisá tu agenda regularmente',
        descripcion:
            'Consultar las próximas reservas te permite anticipar materiales, organizar los tiempos y prepararte para cada atención.',
        icono: 'event_available'
    }

];
