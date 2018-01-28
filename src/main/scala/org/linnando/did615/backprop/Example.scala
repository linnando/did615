package org.linnando.did615.backprop

import scala.language.implicitConversions

case class Example(age: Option[Double],
                   workclass: Option[Workclass.Value],
                   fnlwgt: Option[Double],
                   education: Option[Education.Value],
                   educationNum: Option[Double],
                   maritalStatus: Option[MaritalStatus.Value],
                   occupation: Option[Occupation.Value],
                   relationship: Option[Relationship.Value],
                   race: Option[Race.Value],
                   sex: Option[Sex.Value],
                   capitalGain: Option[Double],
                   capitalLoss: Option[Double],
                   hoursPerWeek: Option[Double],
                   nativeCountry: Option[NativeCountry.Value],
                   income: Option[Income.Value]) {
  def inputs: Vector[Double] = Vector(age.getOrElse(0.0)) ++
    Workclass.values.toVector.map(v => if (workclass.contains(v)) 1.0 else 0.0) ++
    Vector(fnlwgt.getOrElse(0.0)) ++
    Education.values.toVector.map(v => if (education.contains(v)) 1.0 else 0.0) ++
    Vector(educationNum.getOrElse(0.0)) ++
    MaritalStatus.values.toVector.map(v => if (maritalStatus.contains(v)) 1.0 else 0.0) ++
    Occupation.values.toVector.map(v => if (occupation.contains(v)) 1.0 else 0.0) ++
    Relationship.values.toVector.map(v => if (relationship.contains(v)) 1.0 else 0.0) ++
    Race.values.toVector.map(v => if (race.contains(v)) 1.0 else 0.0) ++
    Sex.values.toVector.map(v => if (sex.contains(v)) 1.0 else 0.0) ++
    Vector(capitalGain.getOrElse(0.0), capitalLoss.getOrElse(0.0), hoursPerWeek.getOrElse(0.0)) ++
    NativeCountry.values.toVector.map(v => if (nativeCountry.contains(v)) 1.0 else 0.0)

  def outputs: Vector[Double] = Income.values.toVector.map(v => if (income.contains(v)) 1.0 else 0.0)
}

object Example {
  val numberOfInputs = 105
  val numberOfOutputs = 2

  def apply(fields: Vector[Option[Double]]): Example = Example(
    age = fields(0),
    workclass = fields(1).map(v => Workclass(v.toInt)),
    fnlwgt = fields(2),
    education = fields(3).map(v => Education(v.toInt)),
    educationNum = fields(4),
    maritalStatus = fields(5).map(v => MaritalStatus(v.toInt)),
    occupation = fields(6).map(v => Occupation(v.toInt)),
    relationship = fields(7).map(v => Relationship(v.toInt)),
    race = fields(8).map(v => Race(v.toInt)),
    sex = fields(9).map(v => Sex(v.toInt)),
    capitalGain = fields(10),
    capitalLoss = fields(11),
    hoursPerWeek = fields(12),
    nativeCountry = fields(13).map(v => NativeCountry(v.toInt)),
    income = fields(14).map(v => Income(v.toInt))
  )
}

object Workclass extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToWorkclassVal(x: Value): Val = x.asInstanceOf[Val]

  val Private = Val("Private", "Private")
  val SelfEmpNotInc = Val("SelfEmpNotInc", "Self-emp-not-inc")
  val SelfEmpInc = Val("SelfEmpInc", "Self-emp-inc")
  val FederalGov = Val("FederalGov", "Federal-gov")
  val LocalGov = Val("LocalGov", "Local-gov")
  val StateGov = Val("StateGov", "State-gov")
  val WithoutPay = Val("WithoutPay ", "Without-pay")
  val NeverWorked = Val("NeverWorked", "Never-worked")
}

object Education extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToEducationVal(x: Value): Val = x.asInstanceOf[Val]

  val Bachelors = Val("Bachelors", "Bachelors")
  val SomeCollege = Val("SomeCollege", "Some-college")
  val Eleventh = Val("Eleventh", "11th")
  val HsGrad = Val("HsGrad", "HS-grad")
  val ProfSchool = Val("ProfSchool", "Prof-school")
  val AssocAcdm = Val("AssocAcdm", "Assoc-acdm")
  val AssocVoc = Val("AssocVoc", "Assoc-voc")
  val Ninth = Val("Ninth", "9th")
  val SeventhEighth = Val("SeventhEighth", "7th-8th")
  val Twelfth = Val("Twelfth", "12th")
  val Masters = Val("Masters", "Masters")
  val FirstFourth = Val("FirstFourth", "1st-4th")
  val Tenth = Val("Tenth", "10th")
  val Doctorate = Val("Doctorate", "Doctorate")
  val FifthSixth = Val("FifthSixth", "5th-6th")
  val Preschool = Val("Preschool", "Preschool")
}

object MaritalStatus extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToMaritalStatusVal(x: Value): Val = x.asInstanceOf[Val]

  val MarriedCivSpouse = Val("MarriedCivSpouse", "Married-civ-spouse")
  val Divorced = Val("Divorced", "Divorced")
  val NeverMarried = Val("NeverMarried", "Never-married")
  val Separated = Val("Separated", "Separated")
  val Widowed = Val("Widowed", "Widowed")
  val MarriedSpouseAbsent = Val("MarriedSpouseAbsent", "Married-spouse-absent")
  val MarriedAfSpouse = Val("MarriedAfSpouse", "Married-AF-spouse")
}

object Occupation extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToOccupationVal(x: Value): Val = x.asInstanceOf[Val]

  val TechSupport = Val("TechSupport", "Tech-support")
  val CraftRepair = Val("CraftRepair", "Craft-repair")
  val OtherService = Val("OtherService", "Other-service")
  val Sales = Val("Sales", "Sales")
  val ExecManagerial = Val("ExecManagerial", "Exec-managerial")
  val ProfSpecialty = Val("ProfSpecialty", "Prof-specialty")
  val HandlersCleaners = Val("HandlersCleaners", "Handlers-cleaners")
  val MachineOpInspct = Val("MachineOpInspct", "Machine-op-inspct")
  val AdmClerical = Val("AdmClerical", "Adm-clerical")
  val FarmingFishing = Val("FarmingFishing", "Farming-fishing")
  val TransportMoving = Val("TransportMoving", "Transport-moving")
  val PrivHouseServ = Val("PrivHouseServ", "Priv-house-serv")
  val ProtectiveServ = Val("ProtectiveServ", "Protective-serv")
  val ArmedForces = Val("ArmedForces", "Armed-Forces")
}

object Relationship extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToRelationshipVal(x: Value): Val = x.asInstanceOf[Val]

  val Wife = Val("Wife", "Wife")
  val OwnChild = Val("OwnChild", "Own-child")
  val Husband = Val("Husband", "Husband")
  val NotInFamily = Val("NotInFamily", "Not-in-family")
  val OtherRelative = Val("OtherRelative", "Other-relative")
  val Unmarried = Val("Unmarried", "Unmarried")
}

object Race extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToRaceVal(x: Value): Val = x.asInstanceOf[Val]

  val White = Val("White", "White")
  val AsianPacIslander = Val("AsianPacIslander", "Asian-Pac-Islander")
  val AmerIndianEskimo = Val("AmerIndianEskimo", "Amer-Indian-Eskimo")
  val Other = Val("Other", "Other")
  val Black = Val("Black", "Black")
}

object Sex extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToSexVal(x: Value): Val = x.asInstanceOf[Val]

  val Female = Val("Female", "Female")
  val Male = Val("Male", "Male")
}

object NativeCountry extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToNativeCountryVal(x: Value): Val = x.asInstanceOf[Val]

  val UnitedStates = Val("UnitedStates", "United-States")
  val Cambodia = Val("Cambodia", "Cambodia")
  val England = Val("England", "England")
  val PuertoRico = Val("PuertoRico", "Puerto-Rico")
  val Canada = Val("Canada", "Canada")
  val Germany = Val("Germany", "Germany")
  val OutlyingUS = Val("OutlyingUS", "Outlying-US(Guam-USVI-etc)")
  val India = Val("India", "India")
  val Japan = Val("Japan", "Japan")
  val Greece = Val("Greece", "Greece")
  val South = Val("South", "South")
  val China = Val("China", "China")
  val Cuba = Val("Cuba", "Cuba")
  val Iran = Val("Iran", "Iran")
  val Honduras = Val("Honduras", "Honduras")
  val Philippines = Val("Philippines", "Philippines")
  val Italy = Val("Italy", "Italy")
  val Poland = Val("Poland", "Poland")
  val Jamaica = Val("Jamaica", "Jamaica")
  val Vietnam = Val("Vietnam", "Vietnam")
  val Mexico = Val("Mexico", "Mexico")
  val Portugal = Val("Portugal", "Portugal")
  val Ireland = Val("Ireland", "Ireland")
  val France = Val("France", "France")
  val DominicanRepublic = Val("DominicanRepublic", "Dominican-Republic")
  val Laos = Val("Laos", "Laos")
  val Ecuador = Val("Ecuador", "Ecuador")
  val Taiwan = Val("Taiwan", "Taiwan")
  val Haiti = Val("Haiti", "Haiti")
  val Columbia = Val("Columbia", "Columbia")
  val Hungary = Val("Hungary", "Hungary")
  val Guatemala = Val("Guatemala", "Guatemala")
  val Nicaragua = Val("Nicaragua", "Nicaragua")
  val Scotland = Val("Scotland", "Scotland")
  val Thailand = Val("Thailand", "Thailand")
  val Yugoslavia = Val("Yugoslavia", "Yugoslavia")
  val ElSalvador = Val("ElSalvador", "El-Salvador")
  val TrinidadAndTobago = Val("TrinidadAndTobago", "Trinadad&Tobago")
  val Peru = Val("Peru", "Peru")
  val Hong = Val("Hong", "Hong")
  val HolandNetherlands = Val("HolandNetherlands", "Holand-Netherlands")
}

object Income extends Enumeration {

  protected case class Val(name: String, desc: String) extends super.Val(name)

  implicit def valueToIncomeVal(x: Value): Val = x.asInstanceOf[Val]

  val High = Val("High", ">50K")
  val Low = Val("Low", "<=50K")
}
