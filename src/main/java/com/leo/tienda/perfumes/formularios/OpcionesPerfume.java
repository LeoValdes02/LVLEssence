package com.leo.tienda.perfumes.formularios;

import java.util.List;

public final class OpcionesPerfume {
    private OpcionesPerfume() {}
    public static final List<String> MARCAS = List.of("Dior", "Chanel", "Lancome", "Giorgio Armani", "Yves Saint Laurent", "Paco Rabanne", "Dolce & Gabbana", "Versace", "Gucci", "Hermes", "Prada");
    public static final List<String> CONCENTRACIONES = List.of("Eau de Cologne", "Eau de Toilette", "Eau de Parfum", "Parfum", "Extrait de Parfum");
    public static final List<Integer> TAMANOS = List.of(30, 50, 75, 90, 100, 125, 150, 200);
    public static final List<String> GENEROS = List.of("Hombre", "Mujer", "Unisex");
    public static final List<String> NOTAS = List.of("Bergamota", "Limon", "Lavanda", "Rosa", "Jazmin", "Vainilla", "Sandalo", "Cedro", "Ambar", "Almizcle", "Naranja", "Pimienta", "Iris", "Pachuli");
    /** Reconoce datos escritos por versiones anteriores sin migrar H2. */
    public static String normalizarNota(String nota) {
        if (nota == null) return null;
        return switch (nota.trim()) {
            case "Lim" + "?" + "n", "Lim\u00f3n" -> "Limon";
            case "Jazm" + "?" + "n", "Jazm\u00edn" -> "Jazmin";
            case "S" + "?" + "ndalo", "S\u00e1ndalo" -> "Sandalo";
            case "?" + "mbar", "\u00c1mbar" -> "Ambar";
            case "Pachul" + "?", "Pachul\u00ed" -> "Pachuli";
            default -> nota.trim();
        };
    }
}

