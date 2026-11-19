
export interface ConsejoBelleza {
    id: number;
    titulo: string;
    descripcion: string;
    categoria: string;
    icono: string;
}

export const CONSEJOS_BELLEZA: ConsejoBelleza[] = [

    {
        id: 1,
        titulo: 'La protección solar es fundamental',
        descripcion:
            'Cuando estés al aire libre, utilizá protector solar ' +
            'de amplio espectro, FPS 30 o superior. Recordá ' +
            'reaplicarlo según las indicaciones del producto.',
        categoria: 'Cuidado de la piel',
        icono: 'wb_sunny'
    },

    {
        id: 2,
        titulo: 'La hidratación también es cuidado',
        descripcion:
            'Después de limpiar tu piel, aplicá una crema ' +
            'hidratante adecuada para tus necesidades. ' +
            'Una rutina sencilla puede ser suficiente.',
        categoria: 'Cuidado de la piel',
        icono: 'water_drop'
    },

    {
        id: 3,
        titulo: 'Cuidá tu cabello del calor',
        descripcion:
            'Limitá el uso de temperaturas elevadas al ' +
            'secar o peinar el cabello. El calor excesivo ' +
            'puede favorecer el daño de la fibra capilar.',
        categoria: 'Cabello',
        icono: 'air'
    },

    {
        id: 4,
        titulo: 'Tus uñas también necesitan atención',
        descripcion:
            'Mantené las uñas limpias y secas, limalas ' +
            'suavemente y evitá retirar las cutículas.',
        categoria: 'Uñas',
        icono: 'back_hand'
    },

    {
        id: 5,
        titulo: 'Una rutina no necesita muchos productos',
        descripcion:
            'Elegí productos que respondan a las necesidades ' +
            'de tu piel, en lugar de incorporar demasiados ' +
            'pasos al mismo tiempo.',
        categoria: 'Autocuidado',
        icono: 'spa'
    },

    {
        id: 6,
        titulo: 'Limpiá tus herramientas de maquillaje',
        descripcion:
            'Mantené las brochas y esponjas limpias ' +
            'y dejalas secar correctamente antes ' +
            'de volver a utilizarlas.',
        categoria: 'Maquillaje',
        icono: 'brush'
    },

    {
        id: 7,
        titulo: 'Dedicá unos minutos a vos',
        descripcion:
            'Una pausa para descansar, organizar tus ' +
            'pensamientos o disfrutar de una actividad ' +
            'tranquila también puede formar parte ' +
            'de tu bienestar.',
        categoria: 'Bienestar',
        icono: 'favorite'
    }
];