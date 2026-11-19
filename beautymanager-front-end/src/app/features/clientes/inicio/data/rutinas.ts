
export interface PasoRutina {
    id: string;
    descripcion: string;
}

export interface RutinaBelleza {
    id: string;
    nombre: string;
    descripcion: string;
    icono: string;
    pasos: PasoRutina[];
}


//========================
//Rutinas de autocuidado
//========================

export const RUTINAS_BELLEZA: RutinaBelleza[] = [

    {
        id: 'manana',
        nombre: 'Rutina de mañana',
        descripcion:
            'Comenzá el día dedicándote unos minutos.',
        icono: 'wb_sunny',
        pasos: [
            {
                id: 'manana-1',
                descripcion:
                    'Limpiar el rostro suavemente.'
            },
            {
                id: 'manana-2',
                descripcion:
                    'Aplicar crema hidratante.'
            },
            {
                id: 'manana-3',
                descripcion:
                    'Aplicar protección solar si corresponde.'
            },
            {
                id: 'manana-4',
                descripcion:
                    'Dedicar unos minutos a organizar el día.'
            }
        ]
    },

    {
        id: 'noche',
        nombre: 'Rutina de noche',
        descripcion:
            'Un momento tranquilo para terminar el día.',
        icono: 'nights_stay',
        pasos: [
            {
                id: 'noche-1',
                descripcion:
                    'Retirar el maquillaje si lo utilizaste.'
            },
            {
                id: 'noche-2',
                descripcion:
                    'Limpiar el rostro con suavidad.'
            },
            {
                id: 'noche-3',
                descripcion:
                    'Aplicar tu hidratante habitual.'
            },
            {
                id: 'noche-4',
                descripcion:
                    'Preparar un momento de descanso.'
            }
        ]
    },

    {
        id: 'semanal',
        nombre: 'Momento de autocuidado',
        descripcion:
            'Una pequeña pausa para disfrutar a tu ritmo.',
        icono: 'spa',
        pasos: [
            {
                id: 'semanal-1',
                descripcion:
                    'Elegir un momento sin interrupciones.'
            },
            {
                id: 'semanal-2',
                descripcion:
                    'Realizar algún cuidado personal que disfrutes.'
            },
            {
                id: 'semanal-3',
                descripcion:
                    'Escuchar música o hacer una actividad tranquila.'
            },
            {
                id: 'semanal-4',
                descripcion:
                    'Descansar unos minutos.'
            }
        ]
    }
];