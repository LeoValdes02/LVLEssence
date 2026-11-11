/* Comprueba cobertura real; no considera el fallback una traducción. */
const fs = require('node:fs');
const vm = require('node:vm');
let codigo = fs.readFileSync('src/main/resources/static/javascript/i18n.js', 'utf8');
codigo = codigo.replace('    const originalesTexto = new WeakMap();', '    globalThis.diccionariosPrueba = {diccionario, japones};\n    const originalesTexto = new WeakMap();');
codigo = codigo.replace('let idioma = "es";', 'let idioma = "ja";');
const contexto = {window: {}, localStorage: {getItem: () => null}};
vm.runInNewContext(codigo, contexto);
const {diccionario, japones} = contexto.diccionariosPrueba;
const claves = Object.keys(diccionario);
const faltantes = claves.filter(k => !Object.hasOwn(japones, k) || !japones[k].trim());
const casos = [['2 perfumes encontrados', '2件の香水が見つかりました'], ['Ir a la página 3', '3ページへ'], ['Texto sin traducción', 'Texto sin traducción']];
for (const [origen, esperado] of casos) {
    const actual = contexto.window.LVLI18n.t(origen);
    if (actual !== esperado) { console.error(`${origen}: ${actual} != ${esperado}`); process.exitCode = 1; }
}
console.log(`ES: ${claves.length}; EN: ${claves.length}; JA: ${claves.length - faltantes.length}; faltantes: ${faltantes.length}`);
if (faltantes.length) { console.log(faltantes.join('\n')); process.exitCode = 1; }
