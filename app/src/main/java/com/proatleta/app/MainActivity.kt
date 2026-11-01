package com.proatleta.app

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

val BgDark = Color(0xFF0B1120)
val CardDark = Color(0xFF0F172A)
val InputDark = Color(0xFF1E293B)
val BorderDark = Color(0xFF334155)
val EmeraldPrimary = Color(0xFF10B981)
val EmeraldDark = Color(0xFF064E3B)
val AmberPrimary = Color(0xFFF59E0B)
val TextWhite = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF94A3B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = BgDark,
                    surface = CardDark,
                    primary = EmeraldPrimary,
                    onPrimary = Color(0xFF022C22),
                    onBackground = TextWhite,
                    onSurface = TextWhite
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = BgDark) {
                    ProAtletaApp()
                }
            }
        }
    }
}

val posicoesPorModalidade = mapOf(
    "Futebol de Campo" to listOf(
        "Goleiro", "Lateral Direito", "Lateral Esquerdo", "Zagueiro",
        "Volante", "Meia Central", "Meia Atacante", "Ponta Direita", "Ponta Esquerda", "Centroavante"
    ),
    "Futsal" to listOf(
        "Goleiro (Futsal)", "Fixo", "Ala Direito", "Ala Esquerdo", "Pivô"
    ),
    "Futebol & Futsal" to listOf(
        "Goleiro", "Fixo / Zagueiro", "Ala / Lateral", "Ala / Ponta",
        "Meia / Ala", "Pivô / Centroavante", "Volante / Fixo"
    )
)

@Composable
fun ProAtletaApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("pro_atleta_prefs", Context.MODE_PRIVATE) }

    var abaAtual by remember { mutableStateOf(0) }

    var cadNome by remember { mutableStateOf(prefs.getString("cadNome", "") ?: "") }
    var cadApelido by remember { mutableStateOf(prefs.getString("cadApelido", "") ?: "") }
    var cadIdade by remember { mutableStateOf(prefs.getString("cadIdade", "") ?: "") }
    var cadDocumento by remember { mutableStateOf(prefs.getString("cadDocumento", "") ?: "") }
    var cadTelefone by remember { mutableStateOf(prefs.getString("cadTelefone", "") ?: "") }
    var cadCidade by remember { mutableStateOf(prefs.getString("cadCidade", "") ?: "") }
    var cadResponsavel by remember { mutableStateOf(prefs.getString("cadResponsavel", "") ?: "") }

    var cadModalidade by remember { mutableStateOf(prefs.getString("cadModalidade", "Futebol de Campo") ?: "Futebol de Campo") }
    val listaPosicoes = posicoesPorModalidade[cadModalidade] ?: posicoesPorModalidade["Futebol de Campo"]!!
    var cadPosicao1 by remember { mutableStateOf(prefs.getString("cadPosicao1", listaPosicoes.first()) ?: listaPosicoes.first()) }
    var cadPosicao2 by remember { mutableStateOf(prefs.getString("cadPosicao2", "Nenhuma") ?: "Nenhuma") }
    var cadPe by remember { mutableStateOf(prefs.getString("cadPe", "Destro") ?: "Destro") }

    var cadPeso by remember { mutableStateOf(prefs.getString("cadPeso", "") ?: "") }
    var cadAltura by remember { mutableStateOf(prefs.getString("cadAltura", "") ?: "") }
    var cadCamisa by remember { mutableStateOf(prefs.getString("cadCamisa", "10") ?: "10") }
    var cadUniforme by remember { mutableStateOf(prefs.getString("cadUniforme", "M") ?: "M") }
    var cadChuteira by remember { mutableStateOf(prefs.getString("cadChuteira", "40") ?: "40") }
    var cadSangue by remember { mutableStateOf(prefs.getString("cadSangue", "O+") ?: "O+") }
    var cadObs by remember { mutableStateOf(prefs.getString("cadObs", "") ?: "") }

    var desNome by remember { mutableStateOf(prefs.getString("desNome", "") ?: "") }
    var desPartida by remember { mutableStateOf(prefs.getString("desPartida", "") ?: "") }
    var desCompeticao by remember { mutableStateOf(prefs.getString("desCompeticao", "") ?: "") }
    var desData by remember {
        mutableStateOf(
            prefs.getString("desData", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) ?: ""
        )
    }
    var desModalidade by remember { mutableStateOf(prefs.getString("desModalidade", "Futebol de Campo") ?: "Futebol de Campo") }
    var desPosicao by remember { mutableStateOf(prefs.getString("desPosicao", "Meia / Ala") ?: "Meia / Ala") }
    var desPlacar by remember { mutableStateOf(prefs.getString("desPlacar", "3 x 1") ?: "3 x 1") }
    var desGols by remember { mutableStateOf(prefs.getString("desGols", "0") ?: "0") }
    var desAssistencias by remember { mutableStateOf(prefs.getString("desAssistencias", "0") ?: "0") }
    var desChutes by remember { mutableStateOf(prefs.getString("desChutes", "0") ?: "0") }
    var desChutesAlvo by remember { mutableStateOf(prefs.getString("desChutesAlvo", "0") ?: "0") }
    var desTempo by remember { mutableStateOf(prefs.getString("desTempo", "90") ?: "90") }
    var desStatus by remember { mutableStateOf(prefs.getString("desStatus", "Titular") ?: "Titular") }
    var desNota by remember { mutableStateOf(prefs.getString("desNota", "8.5") ?: "8.5") }
    var desObs by remember { mutableStateOf(prefs.getString("desObs", "") ?: "") }

    fun salvarDados() {
        prefs.edit().apply {
            putString("cadNome", cadNome)
            putString("cadApelido", cadApelido)
            putString("cadIdade", cadIdade)
            putString("cadDocumento", cadDocumento)
            putString("cadTelefone", cadTelefone)
            putString("cadCidade", cadCidade)
            putString("cadResponsavel", cadResponsavel)
            putString("cadModalidade", cadModalidade)
            putString("cadPosicao1", cadPosicao1)
            putString("cadPosicao2", cadPosicao2)
            putString("cadPe", cadPe)
            putString("cadPeso", cadPeso)
            putString("cadAltura", cadAltura)
            putString("cadCamisa", cadCamisa)
            putString("cadUniforme", cadUniforme)
            putString("cadChuteira", cadChuteira)
            putString("cadSangue", cadSangue)
            putString("cadObs", cadObs)

            putString("desNome", desNome)
            putString("desPartida", desPartida)
            putString("desCompeticao", desCompeticao)
            putString("desData", desData)
            putString("desModalidade", desModalidade)
            putString("desPosicao", desPosicao)
            putString("desPlacar", desPlacar)
            putString("desGols", desGols)
            putString("desAssistencias", desAssistencias)
            putString("desChutes", desChutes)
            putString("desChutesAlvo", desChutesAlvo)
            putString("desTempo", desTempo)
            putString("desStatus", desStatus)
            putString("desNota", desNota)
            putString("desObs", desObs)
            apply()
        }
    }

    val idadeNum = cadIdade.toIntOrNull() ?: 0
    val categoriaCalc = when {
        idadeNum in 1..9 -> "Sub-9"
        idadeNum in 10..11 -> "Sub-11"
        idadeNum in 12..13 -> "Sub-13"
        idadeNum in 14..15 -> "Sub-15"
        idadeNum in 16..17 -> "Sub-17"
        idadeNum in 18..20 -> "Sub-20"
        idadeNum > 20 -> "Adulto"
        else -> "Livre"
    }

    val pesoNum = cadPeso.replace(",", ".").toDoubleOrNull() ?: 0.0
    val alturaRaw = cadAltura.replace(",", ".").toDoubleOrNull() ?: 0.0
    val alturaMetros = if (alturaRaw > 3.0) alturaRaw / 100.0 else alturaRaw
    val imcCalc = if (pesoNum > 0 && alturaMetros > 0) pesoNum / (alturaMetros * alturaMetros) else 0.0
    val imcTexto = if (imcCalc > 0) String.format(Locale.US, "%.1f", imcCalc) else "-"
    val imcStatus = when {
        imcCalc == 0.0 -> "Preencha peso/altura"
        imcCalc < 18.5 -> "Abaixo do peso"
        imcCalc < 25.0 -> "Normal (Atleta)"
        else -> "Sobrepeso"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(EmeraldDark, CardDark)))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚽", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PRÓ-ATLETA • PORTAL DO JOGADOR",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Futebol & Futsal • Ficha Cadastral e Desempenho em PDF",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgDark)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { abaAtual = 0 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (abaAtual == 0) EmeraldPrimary else Color.Transparent,
                            contentColor = if (abaAtual == 0) Color(0xFF022C22) else TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("1. Ficha Cadastral", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (desNome.isBlank() && cadNome.isNotBlank()) {
                                desNome = if (cadApelido.isNotBlank()) "$cadNome ($cadApelido)" else cadNome
                                desPosicao = cadPosicao1
                            }
                            abaAtual = 1
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (abaAtual == 1) AmberPrimary else Color.Transparent,
                            contentColor = if (abaAtual == 1) Color(0xFF022C22) else TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("2. Desempenho", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (abaAtual == 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoCardMini(
                        titulo = "IDADE / CATEGORIA",
                        valor = if (idadeNum > 0) "$idadeNum anos" else "-",
                        subtitulo = categoriaCalc,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCardMini(
                        titulo = "ÍNDICE IMC",
                        valor = "IMC $imcTexto",
                        subtitulo = imcStatus,
                        modifier = Modifier.weight(1f)
                    )
                }

                SecaoCard(titulo = "1. DADOS PESSOAIS DO ATLETA") {
                    CampoTexto("Nome Completo do Atleta *", cadNome) { cadNome = it; salvarDados() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Apelido / Camisa", cadApelido, Modifier.weight(1f)) { cadApelido = it; salvarDados() }
                        CampoTexto("Idade Atual *", cadIdade, Modifier.weight(1f), KeyboardType.Number) { cadIdade = it; salvarDados() }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("RG ou CPF", cadDocumento, Modifier.weight(1f)) { cadDocumento = it; salvarDados() }
                        CampoTexto("WhatsApp *", cadTelefone, Modifier.weight(1f), KeyboardType.Phone) { cadTelefone = it; salvarDados() }
                    }
                    CampoTexto("Cidade / Bairro", cadCidade) { cadCidade = it; salvarDados() }

                    if (idadeNum in 1..17) {
                        CampoTexto("Nome do Responsável (Menor de 18) *", cadResponsavel) { cadResponsavel = it; salvarDados() }
                    }
                }

                SecaoCard(titulo = "2. MODALIDADE, POSIÇÃO & FÍSICO") {
                    SeletorOpcoes(
                        label = "Modalidade Esportiva *",
                        opcoes = listOf("Futebol de Campo", "Futsal", "Futebol & Futsal"),
                        selecionado = cadModalidade
                    ) { novaMod ->
                        cadModalidade = novaMod
                        val novasPos = posicoesPorModalidade[novaMod] ?: listOf("Goleiro")
                        cadPosicao1 = novasPos.first()
                        salvarDados()
                    }

                    SeletorOpcoes(
                        label = "Posição Principal *",
                        opcoes = listaPosicoes,
                        selecionado = cadPosicao1
                    ) { cadPosicao1 = it; salvarDados() }

                    SeletorOpcoes(
                        label = "Posição Secundária",
                        opcoes = listOf("Nenhuma") + listaPosicoes,
                        selecionado = cadPosicao2
                    ) { cadPosicao2 = it; salvarDados() }

                    SeletorOpcoes(
                        label = "Pé Dominante",
                        opcoes = listOf("Destro", "Canhoto", "Ambidestro"),
                        selecionado = cadPe
                    ) { cadPe = it; salvarDados() }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Peso (kg) *", cadPeso, Modifier.weight(1f), KeyboardType.Decimal) { cadPeso = it; salvarDados() }
                        CampoTexto("Altura (m) *", cadAltura, Modifier.weight(1f), KeyboardType.Decimal) { cadAltura = it; salvarDados() }
                        CampoTexto("Nº Camisa", cadCamisa, Modifier.weight(1f), KeyboardType.Number) { cadCamisa = it; salvarDados() }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Uniforme (P/M/G)", cadUniforme, Modifier.weight(1f)) { cadUniforme = it; salvarDados() }
                        CampoTexto("Nº Chuteira", cadChuteira, Modifier.weight(1f), KeyboardType.Number) { cadChuteira = it; salvarDados() }
                        CampoTexto("Tipo Sanguíneo", cadSangue, Modifier.weight(1f)) { cadSangue = it; salvarDados() }
                    }

                    CampoTexto("Observações Médicas / Histórico Esportivo", cadObs) { cadObs = it; salvarDados() }
                }

                Button(
                    onClick = {
                        gerarECompartilharPdfCadastro(
                            context = context,
                            nome = cadNome.ifBlank { "Atleta" },
                            apelido = cadApelido.ifBlank { "-" },
                            idade = if (idadeNum > 0) "$idadeNum anos" else "-",
                            categoria = categoriaCalc,
                            documento = cadDocumento.ifBlank { "-" },
                            telefone = cadTelefone.ifBlank { "-" },
                            cidade = cadCidade.ifBlank { "-" },
                            responsavel = if (idadeNum in 1..17) cadResponsavel.ifBlank { "Não informado" } else "Maior de Idade / N/A",
                            modalidade = cadModalidade,
                            posicao1 = cadPosicao1,
                            posicao2 = cadPosicao2,
                            pe = cadPe,
                            peso = if (pesoNum > 0) "$cadPeso kg" else "-",
                            altura = if (alturaMetros > 0) String.format(Locale.US, "%.2f m", alturaMetros) else "-",
                            imc = "$imcTexto ($imcStatus)",
                            camisa = "#$cadCamisa",
                            uniforme = cadUniforme,
                            chuteira = cadChuteira,
                            sangue = cadSangue,
                            obs = cadObs.ifBlank { "Nenhuma observação médica ou restrição informada." }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "📄 SALVAR E COMPARTILHAR PDF (FICHA CADASTRAL)",
                        color = Color(0xFF022C22),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            } else {
                val golsInt = desGols.toIntOrNull() ?: 0
                val assInt = desAssistencias.toIntOrNull() ?: 0
                val chutesInt = desChutes.toIntOrNull() ?: 0
                val alvoInt = desChutesAlvo.toIntOrNull() ?: 0
                val precisao = if (chutesInt > 0) ((alvoInt.toDouble() / chutesInt) * 100).roundToInt().coerceAtMost(100) else 0

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoCardMini("GOLS + ASSIST.", "${golsInt + assInt}", "Participações diretas", Modifier.weight(1f))
                    InfoCardMini("PONTARIA", "$precisao%", "$alvoInt no alvo de $chutesInt chutes", Modifier.weight(1f))
                }

                SecaoCard(titulo = "1. IDENTIFICAÇÃO DO ATLETA E PARTIDA") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dados do Jogo", color = AmberPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Puxar Nome do Cadastro",
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                desNome = if (cadApelido.isNotBlank()) "$cadNome ($cadApelido)" else cadNome
                                desPosicao = cadPosicao1
                                salvarDados()
                            }
                        )
                    }

                    CampoTexto("Nome do Atleta *", desNome) { desNome = it; salvarDados() }
                    CampoTexto("Qual foi a Partida / Adversário? *", desPartida) { desPartida = it; salvarDados() }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Campeonato / Fase", desCompeticao, Modifier.weight(1f)) { desCompeticao = it; salvarDados() }
                        CampoTexto("Data", desData, Modifier.weight(1f)) { desData = it; salvarDados() }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Posição no Jogo", desPosicao, Modifier.weight(1f)) { desPosicao = it; salvarDados() }
                        CampoTexto("Placar (Ex: 3 x 1)", desPlacar, Modifier.weight(1f)) { desPlacar = it; salvarDados() }
                    }

                    SeletorOpcoes(
                        label = "Modalidade da Partida",
                        opcoes = listOf("Futebol de Campo", "Futsal"),
                        selecionado = desModalidade
                    ) { desModalidade = it; salvarDados() }
                }

                SecaoCard(titulo = "2. ESTATÍSTICAS DO ATLETA NA PARTIDA") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("⚽ Gols Marcados", desGols, Modifier.weight(1f), KeyboardType.Number) { desGols = it; salvarDados() }
                        CampoTexto("🎯 Assistências", desAssistencias, Modifier.weight(1f), KeyboardType.Number) { desAssistencias = it; salvarDados() }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("🚀 Chutes Totais", desChutes, Modifier.weight(1f), KeyboardType.Number) { desChutes = it; salvarDados() }
                        CampoTexto("⏱️ Tempo (minutos)", desTempo, Modifier.weight(1f), KeyboardType.Number) { desTempo = it; salvarDados() }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto("Chutes no Gol (Alvo)", desChutesAlvo, Modifier.weight(1f), KeyboardType.Number) { desChutesAlvo = it; salvarDados() }
                        CampoTexto("Nota da Atuação (1-10)", desNota, Modifier.weight(1f), KeyboardType.Decimal) { desNota = it; salvarDados() }
                    }

                    SeletorOpcoes(
                        label = "Status de Participação",
                        opcoes = listOf("Titular", "Jogo Inteiro", "Entrou no 2º Tempo"),
                        selecionado = desStatus
                    ) { desStatus = it; salvarDados() }

                    CampoTexto("Destaques da Atuação / Observações", desObs) { desObs = it; salvarDados() }
                }

                Button(
                    onClick = {
                        gerarECompartilharPdfDesempenho(
                            context = context,
                            nome = desNome.ifBlank { cadNome.ifBlank { "Atleta" } },
                            partida = desPartida.ifBlank { "Partida Oficial" },
                            competicao = desCompeticao.ifBlank { "Amistoso / Campeonato" },
                            data = desData,
                            modalidade = desModalidade,
                            posicao = desPosicao,
                            placar = desPlacar,
                            gols = golsInt,
                            assistencias = assInt,
                            chutes = chutesInt,
                            chutesAlvo = alvoInt,
                            precisao = precisao,
                            tempo = desTempo.ifBlank { "90" },
                            status = desStatus,
                            nota = desNota.ifBlank { "8.5" },
                            obs = desObs.ifBlank { "Atuação registrada pelo sistema Pró-Atleta." }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "🔥 SALVAR E COMPARTILHAR PDF (DESEMPENHO)",
                        color = Color(0xFF022C22),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCardMini(titulo: String, valor: String, subtitulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(titulo, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(valor, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitulo, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SecaoCard(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = titulo, color = EmeraldPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Divider(color = BorderDark)
        content()
    }
}

@Composable
fun CampoTexto(
    label: String,
    valor: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputDark,
                unfocusedContainerColor = InputDark,
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = BorderDark,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorOpcoes(
    label: String,
    opcoes: List<String>,
    selecionado: String,
    onSelect: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = selecionado,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputDark,
                    unfocusedContainerColor = InputDark,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )
            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.background(CardDark)
            ) {
                opcoes.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(opcao, color = TextWhite) },
                        onClick = {
                            onSelect(opcao)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

fun gerarECompartilharPdfCadastro(
    context: Context,
    nome: String,
    apelido: String,
    idade: String,
    categoria: String,
    documento: String,
    telefone: String,
    cidade: String,
    responsavel: String,
    modalidade: String,
    posicao1: String,
    posicao2: String,
    pe: String,
    peso: String,
    altura: String,
    imc: String,
    camisa: String,
    uniforme: String,
    chuteira: String,
    sangue: String,
    obs: String
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    paint.color = AndroidColor.parseColor("#064E3B")
    canvas.drawRect(0f, 0f, 595f, 135f, paint)
    paint.color = AndroidColor.parseColor("#10B981")
    canvas.drawRect(0f, 130f, 595f, 136f, paint)

    paint.color = AndroidColor.parseColor("#6EE7B7")
    paint.textSize = 10f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("FICHA OFICIAL DE CADASTRO DE ATLETA • PRÓ-ATLETA", 32f, 36f, paint)

    paint.color = AndroidColor.WHITE
    paint.textSize = 22f
    canvas.drawText(nome.uppercase(), 32f, 68f, paint)

    paint.color = AndroidColor.parseColor("#E2E8F0")
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText("Apelido: $apelido   |   Categoria: $categoria   |   Camisa: $camisa", 32f, 92f, paint)
    canvas.drawText("Modalidade: $modalidade   |   Posição Principal: $posicao1", 32f, 112f, paint)

    var y = 175f
    desenharTituloSecaoPdf(canvas, "1. IDENTIFICAÇÃO & CONTATO", y)
    y += 22f

    desenharCaixaDadoPdf(canvas, 32f, y, 170f, "IDADE / CATEGORIA", "$idade ($categoria)")
    desenharCaixaDadoPdf(canvas, 212f, y, 170f, "DOCUMENTO (RG/CPF)", documento)
    desenharCaixaDadoPdf(canvas, 392f, y, 170f, "WHATSAPP / CONTATO", telefone)
    y += 58f

    desenharCaixaDadoPdf(canvas, 32f, y, 260f, "CIDADE / BAIRRO", cidade)
    desenharCaixaDadoPdf(canvas, 302f, y, 260f, "RESPONSÁVEL LEGAL", responsavel)
    y += 72f

    desenharTituloSecaoPdf(canvas, "2. ANTROPOMETRIA, MEDIDAS FÍSICAS & POSIÇÃO", y)
    y += 22f

    desenharCaixaDadoPdf(canvas, 32f, y, 125f, "PESO ATUAL", peso)
    desenharCaixaDadoPdf(canvas, 167f, y, 125f, "ALTURA", altura)
    desenharCaixaDadoPdf(canvas, 302f, y, 130f, "ÍNDICE IMC", imc)
    desenharCaixaDadoPdf(canvas, 442f, y, 120f, "PÉ DOMINANTE", pe)
    y += 58f

    desenharCaixaDadoPdf(canvas, 32f, y, 170f, "2ª POSIÇÃO / FUNÇÃO", posicao2)
    desenharCaixaDadoPdf(canvas, 212f, y, 110f, "TAM. UNIFORME", uniforme)
    desenharCaixaDadoPdf(canvas, 332f, y, 110f, "Nº CHUTEIRA", chuteira)
    desenharCaixaDadoPdf(canvas, 452f, y, 110f, "TIPO SANGUÍNEO", sangue)
    y += 72f

    desenharTituloSecaoPdf(canvas, "3. OBSERVAÇÕES MÉDICAS & HISTÓRICO ESPORTIVO", y)
    y += 20f
    desenharCaixaDadoPdf(canvas, 32f, y, 530f, "REGISTRO DO ATLETA", obs)

    paint.color = AndroidColor.parseColor("#64748B")
    paint.strokeWidth = 1f
    canvas.drawLine(50f, 740f, 260f, 740f, paint)
    canvas.drawLine(335f, 740f, 545f, 740f, paint)

    paint.textSize = 10f
    canvas.drawText("Assinatura do Atleta / Responsável", 75f, 756f, paint)
    canvas.drawText("Coordenação Técnica do Projeto", 365f, 756f, paint)

    pdfDocument.finishPage(page)

    val nomeLimpo = nome.replace("\\s+".toRegex(), "_")
    salvarECompartilharArquivoPdf(
        context = context,
        pdfDocument = pdfDocument,
        nomeArquivo = "Ficha_Cadastro_$nomeLimpo.pdf",
        mensagemResumo = "📋 *FICHA CADASTRAL DO ATLETA*\n👤 *Nome:* $nome ($apelido)\n⚽ *Modalidade:* $modalidade ($categoria)\n📍 *Posição:* $posicao1 | *Pé:* $pe\n📏 *Altura:* $altura | *Peso:* $peso"
    )
}

fun gerarECompartilharPdfDesempenho(
    context: Context,
    nome: String,
    partida: String,
    competicao: String,
    data: String,
    modalidade: String,
    posicao: String,
    placar: String,
    gols: Int,
    assistencias: Int,
    chutes: Int,
    chutesAlvo: Int,
    precisao: Int,
    tempo: String,
    status: String,
    nota: String,
    obs: String
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    paint.color = AndroidColor.parseColor("#0F172A")
    canvas.drawRect(0f, 0f, 595f, 150f, paint)
    paint.color = AndroidColor.parseColor("#F59E0B")
    canvas.drawRect(0f, 145f, 595f, 151f, paint)

    paint.color = AndroidColor.parseColor("#FBBF24")
    paint.textSize = 10f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("SCOUT OFICIAL • RELATÓRIO DE DESEMPENHO INDIVIDUAL NA PARTIDA", 32f, 36f, paint)

    paint.color = AndroidColor.WHITE
    paint.textSize = 24f
    canvas.drawText(nome.uppercase(), 32f, 68f, paint)

    paint.color = AndroidColor.parseColor("#CBD5E1")
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText("Partida: $partida   |   Placar: $placar   |   Data: $data", 32f, 96f, paint)
    canvas.drawText("Competição: $competicao   |   Modalidade: $modalidade ($posicao - $status)", 32f, 118f, paint)

    paint.color = AndroidColor.parseColor("#F59E0B")
    canvas.drawRoundRect(RectF(475f, 35f, 560f, 105f), 12f, 12f, paint)
    paint.color = AndroidColor.parseColor("#0F172A")
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9f
    canvas.drawText("NOTA JOGO", 493f, 55f, paint)
    paint.textSize = 24f
    canvas.drawText(nota, 500f, 88f, paint)

    var y = 190f
    desenharTituloSecaoPdf(canvas, "1. ESTATÍSTICAS PRINCIPAIS DO ATLETA NO JOGO", y)
    y += 22f

    desenharCardEstatisticaPdf(canvas, 32f, y, 125f, "GOLS MARCADOS", "$gols", "#059669")
    desenharCardEstatisticaPdf(canvas, 167f, y, 125f, "ASSISTÊNCIAS", "$assistencias", "#2563EB")
    desenharCardEstatisticaPdf(canvas, 302f, y, 125f, "CHUTES TOTAIS", "$chutes", "#D97706")
    desenharCardEstatisticaPdf(canvas, 437f, y, 125f, "TEMPO DE JOGO", "${tempo} min", "#1E293B")
    y += 105f

    desenharTituloSecaoPdf(canvas, "2. APROVEITAMENTO & PARTICIPAÇÃO DIRETA", y)
    y += 22f

    desenharCaixaDadoPdf(canvas, 32f, y, 170f, "PARTICIPAÇÕES (G + A)", "${gols + assistencias} participações")
    desenharCaixaDadoPdf(canvas, 212f, y, 170f, "CHUTES NO ALVO (GOL)", "$chutesAlvo finalizações certas")
    desenharCaixaDadoPdf(canvas, 392f, y, 170f, "PRECISÃO DE CHUTE", "$precisao% de aproveitamento")
    y += 75f

    desenharTituloSecaoPdf(canvas, "3. DESTAQUES DA ATUAÇÃO NA PARTIDA", y)
    y += 22f
    desenharCaixaDadoPdf(canvas, 32f, y, 530f, "ANÁLISE DO JOGO", obs)

    pdfDocument.finishPage(page)

    val nomeLimpo = nome.replace("\\s+".toRegex(), "_")
    salvarECompartilharArquivoPdf(
        context = context,
        pdfDocument = pdfDocument,
        nomeArquivo = "Desempenho_$nomeLimpo.pdf",
        mensagemResumo = "🔥 *DESEMPENHO NA PARTIDA*\n👤 *Atleta:* $nome\n🏟️ *Jogo:* $partida ($placar)\n⚽ *Gols:* $gols | 🎯 *Assistências:* $assistencias\n🚀 *Chutes:* $chutes ($precisao% no alvo) | ⏱️ *Tempo:* ${tempo} min"
    )
}

fun desenharTituloSecaoPdf(canvas: Canvas, titulo: String, y: Float) {
    val paint = Paint().apply {
        color = AndroidColor.parseColor("#047857")
        textSize = 11f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText(titulo, 32f, y, paint)
    paint.color = AndroidColor.parseColor("#CBD5E1")
    paint.strokeWidth = 1f
    canvas.drawLine(32f, y + 6f, 562f, y + 6f, paint)
}

fun desenharCaixaDadoPdf(canvas: Canvas, x: Float, y: Float, largura: Float, label: String, valor: String) {
    val paint = Paint()
    paint.color = AndroidColor.parseColor("#F8FAFC")
    canvas.drawRoundRect(RectF(x, y, x + largura, y + 46f), 8f, 8f, paint)

    paint.color = AndroidColor.parseColor("#CBD5E1")
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(RectF(x, y, x + largura, y + 46f), 8f, 8f, paint)

    paint.style = Paint.Style.FILL
    paint.color = AndroidColor.parseColor("#64748B")
    paint.textSize = 8.5f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(label, x + 10f, y + 16f, paint)

    paint.color = AndroidColor.parseColor("#0F172A")
    paint.textSize = 11.5f
    val textoCortado = if (valor.length > 65) valor.take(65) + "..." else valor
    canvas.drawText(textoCortado, x + 10f, y + 35f, paint)
}

fun desenharCardEstatisticaPdf(canvas: Canvas, x: Float, y: Float, largura: Float, titulo: String, valor: String, corHex: String) {
    val paint = Paint()
    paint.color = AndroidColor.parseColor(corHex)
    canvas.drawRoundRect(RectF(x, y, x + largura, y + 80f), 12f, 12f, paint)

    paint.color = AndroidColor.WHITE
    paint.textSize = 9f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(titulo, x + 12f, y + 22f, paint)

    paint.textSize = 28f
    canvas.drawText(valor, x + 12f, y + 62f, paint)
}

fun salvarECompartilharArquivoPdf(
    context: Context,
    pdfDocument: PdfDocument,
    nomeArquivo: String,
    mensagemResumo: String
) {
    try {
        val pastaPdf = File(context.cacheDir, "pdfs")
        if (!pastaPdf.exists()) pastaPdf.mkdirs()

        val arquivo = File(pastaPdf, nomeArquivo)
        FileOutputStream(arquivo).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            arquivo
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, mensagemResumo)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Compartilhar PDF via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao gerar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
