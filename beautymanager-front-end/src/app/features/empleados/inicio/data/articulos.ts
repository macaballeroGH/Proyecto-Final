
//========================================
//INTERFAZ DE ARTÍCULO PROFESIONAL
//========================================

export interface ArticuloProfesional {
    id: number;
    categoria: string;
    titulo: string;
    descripcion: string;
    icono: string;
    tiempoLectura: string;
    contenido: string[];
}


//========================================
//CATEGORÍAS DE ARTÍCULOS
//========================================

export const CATEGORIAS_ARTICULOS_PROFESIONALES: string[] = [
    'Todos',
    'Atención al cliente',
    'Organización',
    'Higiene',
    'Bienestar profesional'
];


//========================================
//ARTÍCULOS PROFESIONALES
//========================================

export const ARTICULOS_PROFESIONALES: ArticuloProfesional[] = [

    //====================================
    //ATENCIÓN AL CLIENTE
    //====================================

    {
        id: 1,
        categoria: 'Atención al cliente',
        titulo: 'La importancia de una buena primera impresión',
        descripcion:
            'Pequeños detalles que ayudan a crear una experiencia agradable desde el primer momento.',
        icono: 'support_agent',
        tiempoLectura: '3 min',
        contenido: [
            'La experiencia de una clienta comienza antes de recibir el tratamiento. La bienvenida, la organización del espacio y la forma de comunicarse pueden influir en cómo se siente durante su visita.',

            'Una atención cordial incluye escuchar sus necesidades, explicar el servicio de manera comprensible y respetar sus preferencias.',

            'También es importante evitar las interrupciones innecesarias y procurar que cada persona se sienta atendida con tranquilidad y respeto.',

            'Recordar pequeños detalles, mantener una actitud amable y ofrecer información clara contribuye a construir una relación de confianza.'
        ]
    },

    {
        id: 2,
        categoria: 'Atención al cliente',
        titulo: 'Comunicación profesional y escucha activa',
        descripcion:
            'Cómo mejorar la comunicación y comprender las expectativas de cada clienta.',
        icono: 'record_voice_over',
        tiempoLectura: '4 min',
        contenido: [
            'La comunicación es una herramienta fundamental en los servicios de estética. Escuchar atentamente permite comprender las expectativas de cada persona antes de comenzar.',

            'La escucha activa implica prestar atención, evitar interrupciones innecesarias y realizar preguntas cuando sea necesario aclarar alguna información.',

            'También es importante explicar las características del servicio, sus limitaciones y los cuidados correspondientes de manera comprensible.',

            'Una comunicación respetuosa ayuda a prevenir malentendidos y contribuye a que las clientas se sientan cómodas y seguras.'
        ]
    },


    //====================================
    //ORGANIZACIÓN
    //====================================

    {
        id: 3,
        categoria: 'Organización',
        titulo: 'Cómo organizar una jornada de trabajo',
        descripcion:
            'Ideas para distribuir mejor el tiempo y preparar las atenciones del día.',
        icono: 'event_note',
        tiempoLectura: '4 min',
        contenido: [
            'Una jornada bien organizada comienza con una revisión de los turnos programados. Conocer los horarios y servicios reservados ayuda a anticipar las necesidades de cada atención.',

            'Preparar los materiales con anticipación y mantener el espacio ordenado permite reducir interrupciones durante el trabajo.',

            'También conviene contemplar los tiempos necesarios para acondicionar el espacio entre clientas y realizar las tareas de cierre.',

            'Revisar periódicamente la agenda permite detectar cambios, cancelaciones o nuevas reservas y adaptar la organización del día.'
        ]
    },

    {
        id: 4,
        categoria: 'Organización',
        titulo: 'La importancia de un espacio de trabajo ordenado',
        descripcion:
            'Cómo una buena organización del espacio puede facilitar las tareas cotidianas.',
        icono: 'inventory_2',
        tiempoLectura: '3 min',
        contenido: [
            'El orden del espacio de trabajo influye tanto en la comodidad profesional como en la experiencia de las clientas.',

            'Disponer los materiales de uso frecuente en lugares accesibles facilita el desarrollo de las tareas y permite reducir desplazamientos innecesarios.',

            'Separar los elementos limpios de aquellos que requieren limpieza o procesamiento es una práctica importante para mantener condiciones de trabajo adecuadas.',

            'Al finalizar cada atención, dedicar tiempo a reorganizar el espacio contribuye a comenzar el siguiente servicio en mejores condiciones.'
        ]
    },


    //====================================
    //HIGIENE
    //====================================

    {
        id: 5,
        categoria: 'Higiene',
        titulo: 'Buenas prácticas de higiene en centros estéticos',
        descripcion:
            'La importancia del cuidado del espacio, los materiales y los procedimientos de limpieza.',
        icono: 'health_and_safety',
        tiempoLectura: '4 min',
        contenido: [
            'La higiene es una parte esencial del funcionamiento de un centro estético. Cada servicio requiere condiciones apropiadas de limpieza y preparación.',

            'Los instrumentos reutilizables deben recibir el tratamiento de limpieza, desinfección o esterilización que corresponda según su uso, siguiendo los protocolos aplicables.',

            'Es importante comprobar el estado de los materiales, respetar las indicaciones de los productos y mantener organizado el espacio de trabajo.',

            'Estas recomendaciones son generales y no reemplazan los procedimientos sanitarios específicos de cada actividad.'
        ]
    },

    {
        id: 6,
        categoria: 'Higiene',
        titulo: 'Preparación del espacio entre atenciones',
        descripcion:
            'Aspectos esenciales para acondicionar correctamente el lugar antes de recibir a la siguiente clienta.',
        icono: 'cleaning_services',
        tiempoLectura: '3 min',
        contenido: [
            'Entre una atención y la siguiente es necesario acondicionar el espacio de acuerdo con el servicio realizado y los protocolos correspondientes.',

            'Esto puede incluir retirar materiales utilizados, realizar la higiene de las superficies y preparar los elementos necesarios para la próxima atención.',

            'Los productos de limpieza y desinfección deben utilizarse siguiendo las indicaciones de sus fabricantes y las normas aplicables.',

            'Una preparación cuidadosa contribuye a mantener un entorno organizado y adecuado para las actividades del centro.'
        ]
    },


    //====================================
    //BIENESTAR PROFESIONAL
    //====================================

    {
        id: 7,
        categoria: 'Bienestar profesional',
        titulo: 'El autocuidado durante la jornada laboral',
        descripcion:
            'Hábitos sencillos para trabajar con mayor comodidad y cuidar tu bienestar.',
        icono: 'favorite_border',
        tiempoLectura: '3 min',
        contenido: [
            'Quienes trabajan en el ámbito de la estética suelen realizar actividades que requieren concentración, movimientos repetitivos y determinadas posturas durante períodos prolongados.',

            'Organizar el espacio de forma cómoda, realizar pausas cuando sea posible y prestar atención a la postura puede ayudar a reducir molestias.',

            'El bienestar profesional también incluye mantener una buena comunicación con el equipo y organizar las tareas de manera realista.',

            'Dedicar pequeños momentos al propio cuidado favorece una experiencia laboral más equilibrada.'
        ]
    },

    {
        id: 8,
        categoria: 'Bienestar profesional',
        titulo: 'La importancia de las pausas durante el trabajo',
        descripcion:
            'Por qué los descansos forman parte de una jornada de trabajo saludable.',
        icono: 'self_improvement',
        tiempoLectura: '3 min',
        contenido: [
            'Las jornadas con múltiples atenciones pueden requerir períodos prolongados de concentración y actividad física.',

            'Cuando la organización del trabajo lo permite, realizar pausas ayuda a interrumpir las posturas sostenidas y los movimientos repetitivos.',

            'Durante esos momentos es posible descansar, hidratarse y realizar pequeños cambios de postura.',

            'Planificar los tiempos de trabajo de forma razonable permite prestar atención tanto a las necesidades de las clientas como al propio bienestar.'
        ]
    }

];
