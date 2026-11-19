package com.example.beautymanager.Servicio.servicioImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.beautymanager.Modelo.DTO.ArticuloBellezaDTO;
import com.example.beautymanager.Servicio.ArticuloBellezaService;

@Service
public class ArticuloBellezaServiceImpl implements ArticuloBellezaService {

    private final RestClient restClient;

    private static final String[] FUENTES = {
        "https://www.allure.com/feed/rss",
        "https://www.vogue.mx/feed/rss"
    };

    private static final Set<String> PALABRAS_ESPANOL = Set.of(
        "el", "la", "los", "las", "de", "del", "en", "con",
        "para", "por", "una", "un", "y", "que", "como",
        "su", "sus", "sobre", "tendencia", "belleza", "piel"
    );

    private static final Set<String> PALABRAS_INGLES = Set.of(
        "the", "and", "for", "with", "from", "this", "that",
        "how", "your", "you", "are", "is", "of", "in",
        "to", "on", "new", "beauty", "skin", "hair"
    );

    private static final String[] PALABRAS_EXCLUIDAS = {
        "horoscope", "horoscopes", "astrology", "astrological",
        "zodiac", "zodiac sign", "horóscopo", "horóscopos",
        "astrología", "astrologia", "signo zodiacal"
    };

    public ArticuloBellezaServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<ArticuloBellezaDTO> obtenerArticulos() {

        Map<String, ArticuloBellezaDTO> articulos = new LinkedHashMap<>();

        for (String fuente : FUENTES) {

            try {

                byte[] contenido = restClient.get()
                        .uri(fuente)
                        .retrieve()
                        .body(byte[].class);

                if (contenido == null || contenido.length == 0) {
                    continue;
                }

                List<ArticuloBellezaDTO> articulosFuente =
                        procesarRSS(contenido, fuente);

                for (ArticuloBellezaDTO articulo : articulosFuente) {

                    String enlace = normalizarEnlace(articulo.getEnlace());

                    if (enlace != null) {
                        articulos.putIfAbsent(enlace, articulo);
                    }
                }

            } catch (Exception e) {
                System.err.println(
                    "No se pudo consultar la fuente RSS: " + fuente
                );
            }
        }

        List<ArticuloBellezaDTO> resultado =
                new ArrayList<>(articulos.values());

        resultado.sort(
            Comparator.comparing(
                ArticuloBellezaDTO::getFechaPublicacion,
                Comparator.nullsLast(Comparator.reverseOrder())
            )
        );

        return resultado;
    }

    private List<ArticuloBellezaDTO> procesarRSS(
            byte[] contenido, String urlFuente) {

        List<ArticuloBellezaDTO> articulos = new ArrayList<>();

        try {

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl", true
            );
            factory.setFeature(
                "http://xml.org/sax/features/external-general-entities", false
            );
            factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities", false
            );

            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            try (InputStream input = new ByteArrayInputStream(contenido)) {

                Document documento =
                        factory.newDocumentBuilder().parse(input);

                NodeList elementos =
                        documento.getElementsByTagName("item");

                for (int i = 0; i < elementos.getLength(); i++) {

                    Node nodo = elementos.item(i);

                    if (nodo.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    Element item = (Element) nodo;

                    String titulo = limpiarHTML(
                        obtenerTexto(item, "title")
                    );

                    String descripcion = limpiarHTML(
                        obtenerTexto(item, "description")
                    );

                    String enlace = obtenerTexto(item, "link");

                    if (titulo == null || titulo.isBlank()
                            || enlace == null || enlace.isBlank()
                            || !esURLValida(enlace)) {
                        continue;
                    }

                    String texto = titulo + " " +
                            (descripcion == null ? "" : descripcion);

                    if (estaExcluido(texto)) {
                        continue;
                    }

                    if (!esIdiomaAdmitido(texto)) {
                        continue;
                    }

                    String categoria = clasificarCategoria(titulo, descripcion);

                    if (categoria == null) {
                        continue;
                    }

                    ArticuloBellezaDTO articulo =
                            new ArticuloBellezaDTO();

                    articulo.setTitulo(titulo);
                    articulo.setDescripcion(descripcion);
                    articulo.setEnlace(enlace);
                    articulo.setFechaPublicacion(
                        convertirFecha(obtenerTexto(item, "pubDate"))
                    );
                    articulo.setFuente(obtenerNombreFuente(urlFuente));
                    articulo.setImagenUrl(obtenerImagen(item));
                    articulo.setCategoria(categoria);

                    articulos.add(articulo);
                }
            }

        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {

            System.err.println("Error al procesar el RSS de: " + urlFuente);
        }

        return articulos;
    }

    private String obtenerTexto(Element elemento, String etiqueta) {

        NodeList nodos = elemento.getElementsByTagName(etiqueta);

        if (nodos.getLength() == 0) {
            return null;
        }

        String texto = nodos.item(0).getTextContent();

        return texto == null ? null : texto.trim();
    }

    private String obtenerImagen(Element item) {

        NodeList elementos = item.getElementsByTagName("enclosure");

        if (elementos.getLength() > 0) {

            Element enclosure = (Element) elementos.item(0);

            String tipo = enclosure.getAttribute("type");
            String url = enclosure.getAttribute("url");

            if (tipo != null && tipo.startsWith("image/")
                    && esURLValida(url)) {
                return url;
            }
        }

        String[] etiquetas = {
            "media:content",
            "media:thumbnail"
        };

        for (String etiqueta : etiquetas) {

            NodeList imagenes = item.getElementsByTagName(etiqueta);

            if (imagenes.getLength() > 0) {

                Element imagen = (Element) imagenes.item(0);
                String url = imagen.getAttribute("url");

                if (esURLValida(url)) {
                    return url;
                }
            }
        }

        return null;
    }

    private boolean esURLValida(String valor) {

        if (valor == null || valor.isBlank()) {
            return false;
        }

        try {

            URI uri = URI.create(valor);

            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String normalizarEnlace(String enlace) {

        if (enlace == null || enlace.isBlank()) {
            return null;
        }

        try {

            URI uri = URI.create(enlace.trim()).normalize();

            String esquema = uri.getScheme();
            String host = uri.getHost();

            if (esquema == null || host == null) {
                return null;
            }

            String ruta = uri.getPath() == null ? "" : uri.getPath();

            return esquema.toLowerCase(Locale.ROOT) + "://"
                    + host.toLowerCase(Locale.ROOT) + ruta;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String obtenerNombreFuente(String url) {

        if (url.contains("allure.com")) {
            return "Allure";
        }

        if (url.contains("vogue.mx")) {
            return "Vogue México";
        }

        return "Fuente externa";
    }

    private LocalDateTime convertirFecha(String fecha) {

        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        try {

            return ZonedDateTime.parse(
                fecha,
                DateTimeFormatter.RFC_1123_DATE_TIME
            ).toLocalDateTime();

        } catch (DateTimeParseException e) {

            try {

                return OffsetDateTime.parse(fecha).toLocalDateTime();

            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }

    private String limpiarHTML(String texto) {

        if (texto == null) {
            return null;
        }

        return texto
                .replaceAll("(?is)<script.*?>.*?</script>", "")
                .replaceAll("(?is)<style.*?>.*?</style>", "")
                .replaceAll("<[^>]*>", " ")
                .replace("&nbsp;", " ")
                .replace("&#160;", " ")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean estaExcluido(String texto) {

        String contenido = texto.toLowerCase(Locale.ROOT);

        for (String palabra : PALABRAS_EXCLUIDAS) {

            if (contenido.contains(palabra)) {
                return true;
            }
        }

        return false;
    }

    private boolean esIdiomaAdmitido(String texto) {

        String[] palabras = texto.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .split("\\s+");

        int coincidenciasEspanol = 0;
        int coincidenciasIngles = 0;

        for (String palabra : palabras) {

            if (PALABRAS_ESPANOL.contains(palabra)) {
                coincidenciasEspanol++;
            }

            if (PALABRAS_INGLES.contains(palabra)) {
                coincidenciasIngles++;
            }
        }

        return coincidenciasEspanol >= 2 || coincidenciasIngles >= 2;
    }

    private String clasificarCategoria(
            String titulo, String descripcion) {

        String texto = ((titulo == null ? "" : titulo) + " "
                + (descripcion == null ? "" : descripcion))
                .toLowerCase(Locale.ROOT);

        if (contienePalabra(texto,
                "makeup", "maquillaje", "lipstick", "mascara",
                "foundation", "blush", "eyeshadow", "eyeliner",
                "labial", "rubor", "sombras")) {
            return "Maquillaje";
        }

        if (contienePalabra(texto,
                "hair", "cabello", "shampoo", "shampú",
                "hairstyle", "peinado", "haircare",
                "acondicionador", "tinte")) {
            return "Cabello";
        }

        if (contienePalabra(texto,
                "nail", "nails", "uñas", "manicure",
                "manicura", "pedicure", "pedicura",
                "nail polish")) {
            return "Uñas";
        }

        if (contienePalabra(texto,
                "skin", "skincare", "skin care", "piel",
                "dermatology", "dermatología", "serum",
                "sérum", "moisturizer", "hidratante",
                "sunscreen", "protector solar", "acne", "acné")) {
            return "Cuidado de la piel";
        }

        if (contienePalabra(texto,
                "perfume", "fragrance", "fragrances",
                "fragancia", "fragancias", "perfumería")) {
            return "Perfumes";
        }

        if (contienePalabra(texto,
                "beauty", "belleza", "cosmetics", "cosmética",
                "cosmetology", "cosmetología", "esthetic",
                "estética", "beauty trend",
                "tendencia de belleza", "tendencias de belleza")) {
            return "Tendencias";
        }

        return null;
    }

    private boolean contienePalabra(String texto, String... palabras) {

        for (String palabra : palabras) {

            if (texto.contains(palabra)) {
                return true;
            }
        }

        return false;
    }
}
