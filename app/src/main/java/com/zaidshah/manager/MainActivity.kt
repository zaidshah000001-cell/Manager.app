package com.zaidshah.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

private val Forest = Color(0xFF2F6B4F)
private val Cream = Color(0xFFF7F4EC)
private fun money(paise:Long):String = "₹" + NumberFormat.getNumberInstance(Locale("en","IN")).format(paise/100.0)
private fun amount(s:String):Long? = s.trim().toBigDecimalOrNull()?.takeIf { it.signum()>0 && it.scale()<=2 }?.multiply(java.math.BigDecimal(100))?.toLongExactOrNull()
private fun java.math.BigDecimal.toLongExactOrNull():Long?=try { longValueExact() } catch(_:Exception){null}
private data class Entry(val type:String,val paise:Long,val note:String)
private data class Debt(val name:String,val type:String,val total:Long,val paid:Long)
class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val prefs=getSharedPreferences("manager_alpha",MODE_PRIVATE);setContent {
 var tab by remember { mutableIntStateOf(0) }
 var name by remember { mutableStateOf(prefs.getString("name","Zaid")?:"Zaid") }
 var dark by remember { mutableStateOf(prefs.getBoolean("dark",false)) }
 var entries by remember { mutableStateOf(loadEntries(prefs.getString("entries","[]")?:"[]")) }
 var debts by remember { mutableStateOf(loadDebts(prefs.getString("debts","[]")?:"[]")) }
 var targets by remember { mutableStateOf(listOf(150000L,1000000L,4000000L)) }
 var dialog by remember { mutableStateOf("") }
 var input by remember { mutableStateOf("") }
 var detail by remember { mutableStateOf("") }
 var debtType by remember { mutableStateOf("Borrowed") }
 var selectedDebt by remember { mutableIntStateOf(-1) }
 var error by remember { mutableStateOf("") }
 val primary=if(dark) Color(0xFF87CBA3) else Forest
 val background=if(dark) Color(0xFF18231D) else Cream
 val surface=if(dark) Color(0xFF29372E) else Color.White
 val ink=if(dark) Color.White else Color(0xFF1C392D)
 fun saveEntries(v:List<Entry>){entries=v;prefs.edit().putString("entries",JSONArray().apply{v.forEach{put(JSONObject().put("type",it.type).put("paise",it.paise).put("note",it.note))}}.toString()).apply()}
 fun saveDebts(v:List<Debt>){debts=v;prefs.edit().putString("debts",JSONArray().apply{v.forEach{put(JSONObject().put("name",it.name).put("type",it.type).put("total",it.total).put("paid",it.paid))}}.toString()).apply()}
 MaterialTheme(colorScheme=if(dark) darkColorScheme(primary=primary,background=background,surface=surface,onSurface=ink) else lightColorScheme(primary=primary,background=background,surface=surface,onSurface=ink)){
 Scaffold(containerColor=background,bottomBar={
 NavigationBar(containerColor=surface){listOf("Home","Goals","Loans","Settings").forEachIndexed{index,title->NavigationBarItem(selected=tab==index,onClick={tab=index},icon={Icon(when(index){0->Icons.Default.Home;1->Icons.Default.Flag;2->Icons.Default.AccountBalanceWallet;else->Icons.Default.Settings},null)},label={Text(title)})}}
 }){pad->
 Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
 Text(when(tab){0->"Hey $name 👋";1->"Your Goals";2->"Loans & Lending";else->"Settings"},style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=ink)
 when(tab){
 0->{
 val earned=entries.filter{it.type=="Earn"}.sumOf{it.paise};val spent=entries.filter{it.type=="Spend"}.sumOf{it.paise}
 Panel(surface){Text("Daily Earnings",fontWeight=FontWeight.Bold);Text(money(earned),style=MaterialTheme.typography.headlineLarge,color=primary);LinearProgressIndicator(progress={if(targets[0]>0)(earned.toFloat()/targets[0]).coerceIn(0f,1f) else 0f},modifier=Modifier.fillMaxWidth());Text("Spent: ${money(spent)}     Net: ${money(earned-spent)}");Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={dialog="Earn";input="";detail=""}){Text("+ Earn")};OutlinedButton(onClick={dialog="Spend";input="";detail=""}){Text("+ Spend")}}}
 Panel(surface){Text("Loans & Lending",fontWeight=FontWeight.Bold);debts.take(3).forEach{Text("${it.name} • ${it.type}: ${money(it.total-it.paid)}")};TextButton(onClick={tab=2}){Text("See loans")}}
 Panel(surface){Text("Recent Transactions",fontWeight=FontWeight.Bold);entries.takeLast(8).reversed().forEach{Text("${it.type}: ${money(it.paise)}  ${it.note}")};if(entries.isEmpty())Text("No transactions yet")}
 }
 1->{
 Panel(surface){Text("Your Goals",fontWeight=FontWeight.Bold);listOf("Daily","Weekly","Monthly").forEachIndexed{i,label->val total=entries.filter{it.type=="Earn"}.sumOf{it.paise};Text("$label • ${money(targets[i])}");LinearProgressIndicator(progress={(total.toFloat()/targets[i]).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth());TextButton(onClick={dialog="Target$i";input="";detail=""}){Text("Edit $label target")}}}
 Panel(surface){Text("Completed Goals");Text("Completed goals will appear here after tracking is implemented.")}
 Panel(surface){Text("Goal History");Text("Contribution history is not enabled in this build.")}
 }
 2->{
 val borrowed=debts.filter{it.type=="Borrowed"}.sumOf{it.total-it.paid};val lent=debts.filter{it.type=="Lent"}.sumOf{it.total-it.paid}
 Panel(surface){Text("Borrowed: ${money(borrowed)}");Text("Lent: ${money(lent)}");Button(onClick={dialog="Loan";input="";detail="";debtType="Borrowed"}){Text("+ Add loan")}}
 debts.forEachIndexed{i,d->Panel(surface){Text(d.name,fontWeight=FontWeight.Bold);Text("${d.type} • Remaining ${money(d.total-d.paid)}");Text("Original: ${money(d.total)}");if(d.total>d.paid)TextButton(onClick={selectedDebt=i;dialog="Repay";input=""}){Text("Record repayment")}}}
 }
 else->{
 Panel(surface){Text("Profile",fontWeight=FontWeight.Bold);OutlinedTextField(value=name,onValueChange={name=it.take(40);prefs.edit().putString("name",name).apply()},label={Text("Display name")})}
 Panel(surface){Text("Appearance",fontWeight=FontWeight.Bold);Row(verticalAlignment=Alignment.CenterVertically){Text("Dark mode");Spacer(Modifier.width(16.dp));Switch(checked=dark,onCheckedChange={dark=it;prefs.edit().putBoolean("dark",it).apply()})}}
 Panel(surface){Text("Manager V1 test build");Text("Local data only. No login, SMS access, UPI payments, or backup. Uninstalling deletes local records.")}
 }
 }
 }
 }
 if(dialog.isNotEmpty())AlertDialog(onDismissRequest={dialog=""},title={Text(when{dialog=="Loan"->"New loan";dialog=="Repay"->"Record repayment";dialog.startsWith("Target")->"Edit goal";else->"Add $dialog"})},text={
 Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
 if(dialog=="Loan"){OutlinedTextField(value=detail,onValueChange={detail=it},label={Text("Person name")});Row{listOf("Borrowed","Lent").forEach{FilterChip(selected=debtType==it,onClick={debtType=it},label={Text(it)})}}}
 OutlinedTextField(value=input,onValueChange={input=it},label={Text("Amount (₹)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
 if(dialog=="Earn"||dialog=="Spend")OutlinedTextField(value=detail,onValueChange={detail=it},label={Text("Note (optional)")})
 if(error.isNotBlank())Text(error,color=MaterialTheme.colorScheme.error)
 }
 },confirmButton={TextButton(onClick={
 val a=amount(input);error=""
 if(a==null){error="Enter a valid positive amount (max 2 decimals)"}else when{
 dialog=="Earn"||dialog=="Spend"->{saveEntries(entries+Entry(dialog,a,detail));dialog=""}
 dialog=="Loan"->{if(detail.isBlank())error="Enter a name" else{saveDebts(debts+Debt(detail.trim(),debtType,a,0));dialog=""}}
 dialog=="Repay"->{val d=debts.getOrNull(selectedDebt);if(d==null||a>d.total-d.paid)error="Amount exceeds remaining balance" else{saveDebts(debts.mapIndexed{i,item->if(i==selectedDebt)item.copy(paid=item.paid+a) else item});dialog=""}}
 dialog.startsWith("Target")->{val i=dialog.removePrefix("Target").toInt();targets=targets.toMutableList().apply{set(i,a)};dialog=""}
 }
 }){Text("Save")}},dismissButton={TextButton(onClick={dialog="";error=""}){Text("Cancel")}})
 }
 }
 }
}
@Composable private fun Panel(bg:Color,content:@Composable ColumnScope.()->Unit){Surface(color=bg,shape=RoundedCornerShape(24.dp),tonalElevation=2.dp,modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp),content=content)}}
private fun loadEntries(s:String):List<Entry> =try{val a=JSONArray(s);(0 until a.length()).map{val o=a.getJSONObject(it);Entry(o.getString("type"),o.getLong("paise"),o.optString("note"))}}catch(_:Exception){emptyList()}
private fun loadDebts(s:String):List<Debt> =try{val a=JSONArray(s);(0 until a.length()).map{val o=a.getJSONObject(it);Debt(o.getString("name"),o.getString("type"),o.getLong("total"),o.getLong("paid"))}}catch(_:Exception){emptyList()}
