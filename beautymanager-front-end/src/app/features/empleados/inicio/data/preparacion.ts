
//========================================
//INTERFAZ DE TAREA DE PREPARACIÓN
//========================================

export interface TareaPreparacion {

    id: string;
    titulo: string;
    descripcion: string;
    icono: string;

}


//========================================
//TAREAS DE PREPARACIÓN DE LA JORNADA
//========================================

export const TAREAS_PREPARACION: TareaPreparacion[] = [

    //====================================
    //REVISAR LA AGENDA
    //====================================

    {
        id: 'revisar-agenda',
        titulo: 'Revisar mi agenda',
        descripcion:
            'Consultar los turnos del día, sus horarios y los servicios programados para organizar las próximas atenciones.',
        icono: 'calendar_month'
    },


    //====================================
    //PREPARAR EL ESPACIO
    //====================================

    {
        id: 'preparar-espacio',
        titulo: 'Preparar el espacio de trabajo',
        descripcion:
            'Comprobar que el área de atención se encuentre limpia, ordenada y preparada para recibir a las clientas.',
        icono: 'cleaning_services'
    },


    //====================================
    //VERIFICAR MATERIALES
    //====================================

    {
        id: 'verificar-materiales',
        titulo: 'Verificar los materiales',
        descripcion:
            'Revisar la disponibilidad y las condiciones de los productos, instrumentos y elementos necesarios para los servicios.',
        icono: 'inventory_2'
    },


    //====================================
    //COMPROBAR LOS EQUIPOS
    //====================================

    {
        id: 'comprobar-equipos',
        titulo: 'Comprobar los equipos',
        descripcion:
            'Verificar que los equipos que se utilizarán estén en condiciones adecuadas y preparados según sus instrucciones de uso.',
        icono: 'fact_check'
    },


    //====================================
    //PREPARACIÓN PERSONAL
    //====================================

    {
        id: 'preparacion-personal',
        titulo: 'Preparación personal',
        descripcion:
            'Organizar los elementos personales necesarios y comprobar que todo esté listo para comenzar la jornada.',
        icono: 'checkroom'
    }

];
