package com.ainnotate.aidas.web.rest;

import liquibase.pro.packaged.A;

import java.sql.*;
import java.util.*;

public class Test {
    public static void main(String[] args){/*
        try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/ainnotateservice", "root", "")) {

            if (conn != null) {
                String query ="select id,id,-1,value from property order by id";
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    System.out.println(rs.getInt(1)+";"+rs.getInt(2)+";"+rs.getInt(3)+";"+rs.getString(4));
                }
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    */
    String orgs = "Scrubs and Bugs,Alss Data Science,Langford Thomson,Zestbook Services,Travis McEnaney,All My Life Data,WakeUp Research,The Dataparallel,Wooey Doody,ScribeDot Solutions,A FactCheck,BlokeyCrazy,SmartAsset dot com,Sensational Research,My Data Funding,Bridges to the Net,Smartphone Lapse,Koolitop,Adrenvish,AllsourceData";
    String customers = "Kaneco Holdings Co.,A Bit of Information,Statamare,Citizen Crib,Tradition of Kings,Intriguing Data,Noodoodletown,FxKap,WizCrap,Hodges & Forbes,Nomads Snack Shack,Qbio Tech Services,Data-Proof.ca,LovinLives,Refinery Progress,Sydney Library,InStrategic Insight,Searache Co.,Xero Xtreme Printing,Zoyo Datamining,Stacked Snooze,Ava's Bodywork,Crowdlives,Acai Berry Buzz,Harmony Intelligence,Influence by Nature,Zariah Law Group,Dollies and Books,Envince Research,A Better Life Source,Insight Data Bank,Scribdigix,Shoot-A-Key,Nuts Berries & More,B. E. Scrivens,Blokelytics,Icy Hotz,Mighty Notch,Nostalgia For One,AJS Data,Quinn Poll,Pasteas Global,All Stedicated,Genetic Speight,Solutions2Money,The Social CMD,Scrubs It Up,Tc Adn,Tonic Research,Fotoola,Truline Labs,Applied Science Data,Lift Log Book,Intentional Source,Wrap It Up Datum,Empire NewsNet,Ynys Cs,Gimme Your Slots,SmartShots.com,Datapoints Direct,Brimmer's Bizarre,The Grammar Store,Pixels of Lead,Toby Keith Services,Sourced Data Systems,Emser Research,Truvilla Analytics,Business Source Data,Qn3Media,Go Data Source,Qcumilat,Acadia Biometrics,Business with Ewe,GetUp and StayUp,Brainstorm Vantage,Alquator Finance,K9HQ.com,CSN Analytics,Dangerous Disposals,Get Data Journalism,H2H Finds,Stampede Reporting,The Datum Collective,Lit-Up Stix,R-Genius Data,Boulder Data Sources,Curatek Data,Data Source IQ,Anhui Gaochen,Ekkpopov,Budwigs DPM,Jareds SEO,Tailored Reprisals,Bry Lintz,Just Scams,Aamco Watson,Newscaster Solutions,Xpointes,Rent a Slicer,GoToMeanData,Kubilka,A+ Business Insight,Fiction Jungle,Rikky's Jerk,Familar Source,Dalekin Media,Yen's Nf,Aussie Tech Data,Sinkle Monkey,The Linguery,Accon Therapes,StratLogic Data,Voltage Research,Intentional Scrap,Data Sources Direct,Quit Chatbots,Cheshire Hathaway,Quimicen,Stahlle & Stahl,Intentional Curves,The Canned Bear,My Little Blackboard,Find Scenarios,Genesee Data,Academic Index Data,Lucky's Pottery,Acorns Pages,Insight Data Library,Abuzz Analytics,Data Crunchy,Ease One Resources,Lap Dog Research,Stapit Bay,Scrunchies By Jo,UCSD Media,Proximus Research,Foam Research,Aurora Bakers,AIM Data Solutions,Evaluate Routine,Macleay Science,Lovingly Indulge,Hemag Arch,Lovingly Infused,Rapidly sourced,AJV Data Solutions.,Plotted In,Bigger Picture Data,GeniCurious,R2U Labs,OneQuark Solutions,Piece Upon Piece,Data Sources Q,Routes & Data,The Batch Game,Flock 'n Fizz,Sourcing Innovations,Forked Fig,Kelley Clark PhD,I'll Wank,Wyrde Marketing,Cleveland Cleats,A/Profiled,Honest-1 Source,The Source Yard,Xpert Gives,Envie Credit Repair,Data by Tanya,Happiness Knows,Lloyds Bankers,Noosa Mining Co,Smoookt,Bing and Cai-Zhang,Ignotam,Envy Library,I Just Dump Dat,CSIRO Entree,Good Eaves,Punktive,AllAboutMe.com,Bodie Genomics,A.A.A.Budgie,Rudy's Rental,Lice On the Go,Hakim Data,DataSourceLoan,Likr Labs,Ancix Logic,Unstoppable Images,Khan's Data,We Market My Booze,Data Source America,Stork Resource,Wireditude,A-Z Data Mining,Adornx Data,Facts N' More,Doodyfied,Diana Crowsfoot,Data-Dependable";
    String vendors = "LizBulk,QdotB Web Tools,Cnn Insights,Vegas News 24,Research HCI,Able Health Records,A Thousand Looms,Q4 News24,Critello,Trimps & Twigs,Randi Data,Survey and Data,We Rate Our Data,Grupo Revolucionaria,Zoomin' Clues,We Source Our Juice,Lunamet Graphics Pty,Go! You Money Guru,Datapoints Finder,Fluffy the Flower,Vizualious,BAMF Tempe Data Bank,QuadCityResearch,Totally Tailed,Virtusson,Clarence K. Paddyny,Go Data Doctors,Truly Big Data,Scribbly the Scuba,Dunn C & L N,Stride 'n Snooze,The Internet Key,Honest-1 Data Corp,Data Science Logs,Logic2Learn,Downtown Dataminr,Uruknet Systems,Xcel Data Science,Shrinkwrap Labs,A.A.D.M.S. Research,Nimbin Media,Saving Fireflies,Stash Me Not,The Data Snob,Rapid Hit Data,Las Vegas Coding,Acme Data Solutions,Brunetec,BakkiKokai,Watmote.com";
    String users = "Magan Swetha,Udit Chinnakannan,Shamindra Gundlapalli,Deeptendu Saandeep,Aiman Chaudhari,Mahavir Yauvani,Prabal Soham Subhuja,Vidyacharan Om Pasram,Shripad Hament,Sudhamay Kodanda,Kantilal Puja,Parnad Shailendra,Salil Salil,Rituparan Seshaanath,Samudrasen Revathi,Devadutt Palanisamy,Gangadhar Kathrada,Mukul Samir,Vibhat Patanjali,Yajnarup Nihar,Nissim Naidoo,Hashmat Nishar,Nairit Swaminath Vidur,Vajra Meka,Bhanuprasad Tarik Ravandur,Indushekhar Vavveti,Abhrakasin Ramnarine,Yadav Parthiban,Shripal Vaidya,Arav Sitha,Rishabh Ramratan Namasri,Amolik Prafull,Swaminath Manasi,Arya Milind,Nayan Chippada,Nityananda Kunderan,Kalidas Puri,Tapas Vidwans,Parijat Raviprakash,Indraneel Chandrark,Pramesh Umakanta,Dilawar Padmanabh,Ishwar Vichur,Nishant Eswara,Farid Saunak,Gajendra Nilima,Satish Sawardekar,Anshul Mukku,Heer Samuel,Hriday Nisheeth,Aashish Vellanki,Vibhishan Panyala,Narsimha Parmar,Muni Pulavarti,Badrinath Karunashankar Sharma,Irfan Apparajito Pashupathy,Yajnarup Indubhushan Shadilya,Sohil Pushkar,Purandar Devnarayan Margasahayam,Parindra Sudhakar,Girik Senagala,Narasimha Pranay Vibhuti,Avanindra Tumkur,Krishanu Nukala,Swapan Sambandan,Achanda Raghavendran,Aruni Sharadindu Sreehari,Anunay Narendra Shreeyash,Kinshuk Yogendra,Praver Rajaram,Satyajit Tapan,Sagun Nandakumar,Vallabh Narayan,Ajendra Laddha,Mohajit Vaninadh,Tamonash Nagappa,Moti Ujjwal,Teerthankar Thirumalaiswamy,Akshan Pusti,Sahaj Kambli,Tribhuvan Muralimanohar Manohar,Akshay Gowda,Vishwamitra Gorti,Priyaranjan Kaviraj Mehul,Girindra Lalitesh,Indukanta Savarna,Amar Gowd,Neelam Sadayappan,Shankha Koganti,Shaukat Kirmani,Pratosh Samderiya,Virochan Vinita,Jasveer Suranjan,Aagney Kailashnath Tatat,Pariket Shailesh,Jagjeevan Rajaraman,Aijaz Neel,Pulish Kitu,Upamanyu Shalaby,Japesh Sarasvan,Rajatshubhra Moorthy,Manibhushan Niramitra,Devnath Suthar,Ratannabha Sathiamoorthy,Mitra Suchitra,Viswanath Shukla,Chaturbhuj Muddiah,Amitiyoti Gaekwad,Manasi Surpur,Sulekh Shubhabrata,Senajit Satyanarayana,Lankesh Shanmukha Swati,Vandan Suryanarayama,Amalendu Solkar,Prithvi Makam,Chintamani Nishar,Pujit Mandar,Nirijhar Gurijala,Sadiq Rabindran,Trilochan Thamry,Hafiz Nimbalkar,Jagannath Sadashiv,Mitul Shrisha,Aakar Vivekanand,Vibhat Sarmistha,Paresh Dasari,Falguni Subbarao,Gupil Nagedwaran,Nirav Nirmal,Shripad Paavan Pewar,Harshvardhan Ramiah,Suvrata Mallika,Shivesh Karna Battacharjee,Rajendra Badesha,Vrajanadan Tamragouri,Sutej Mahabala,Panduranga Surendar,Apparajito Vaidheeswarran,Shriram Maran,Mehdi Arasaratnam,Valmiki Manesh,Rituparan Yogesh,Pranit Venkatasubramani,Jagat Ujjwal,Nairit Suji,Pran Muktheswara,Duranjaya Udit,Nirmalya Kaushal,Pratosh Motala,Abhidi Laddha,Salena Sukumar,Jaya Shreenath,Thumri Ranga,Gangika Kity,Kalavati Rachna,Manjulika Vidya Gurbux,Meena Sarangapani,Jeevankala Naseer,Payal Vajpeyi,Charulata Roshni,Nandita Chirag,Kanta Mona,Ajala Vijayalakshmi Saighiridhar,Shobhana Sudeshna,Meghamala Kaushik,Sona Chhavvi,Sukeshi Chandiramani,Chiti Imani,Anagha Ubriani,Sheela Matta,Shradhdha Manesh,Hemlata Joshi,Shipra Sapra,Anumati Shulka Keshavan,Anita Sruti,Saudamini Kanti Ganapathy,Naina Shyamsundar,Shyla Pushkarini,Shrabana Satyanarayana,Kalindi Jafferbhoy,Jowaki Raviraj,Samit Patankar,Poorbi Podury,Yashaswini Desai,Sunetra Lahan,Mayuka Mathrubootham,Hina Priyadarshini,Dhanashri Ramdas,Shrikumari Arpita Nitesh,Quarrtulain Chandrasekaran,Devasree Damayanti Sreevijayan,Phoolan Motala,Vetravati Nayana Parnika,Ratnapriya Vandita,Jayashree Jitesh,Varsha Syamala,Rishika Paloma,Vishala Maudgalya,Zarine Katyayani Persaud,Neeti Bisht,Pramiti Pushkar,Lajja Sristi Suchin,Padmini Talip,Shankari Maji,Champabati Chittor,Shorashi Shreekant,Medha Dhatri Darsha,Madirakshi Sanigepalli,Pival Mhambrey,Dristi Yavar,Sarbani Saurin,Brinda Kuntal,Foolan Malini Krithivas,Parnik Jandhyala,Neelabja Madugula,Suvarnmala Sukumar,Leelavati Khadri,Nina Matu,Raka Shabana Vivatma,Roshni Sharmistha Swathi,Hindola Prasata,Resham Khodabhai,Jyotishmati Sripadam,Kesari Sury,Deepta Sreeram,Gool Tanmaya,Rangana Rasna Revathi,Gomati Nidra,Anita Shridhar,Trusha Majhi,Pritilata Talwar,Sunita Prerana,Sucharita Chellappa,Kalindi Soundar,Sadiqua Nayak,Godavari Sehgal,Mahijuba Sumanolata Gundugollu,Ruchi Suchitra,Krupa Thommana,Shyamali Narsi,Shyamlata Sashi,Suhrita Vinata,Husna Nitya-sundara,Gopa Sonika Kateel,Pramila Meenakshi,Menaka Akshaya Shinu,Fatima Sornam,Pragyawati Vishwamber,Sunandita Shradhdha,Jaya Sekhar,Ashwini Niharika,Ujjwala Khadri,Vidhut Jitesh,Shobhna Smita,Prama Saunak,Induma Rajarama,Shakti Madan,Sananda Suryakanti Oruganti,Mythily Soumitra,Abani Vedula,Bhagya Vadakke,Tapani Parthasarathy,Vindhya Ghouse,Selma Suji,Priyadarshini Tuhina Subramanian,Isha Jhinuk Venkatesan,Kakali Srihari,Jeeval Ponamgi,Kapila Thadigiri,Seemanti Muppala,Tamalika Kalirai,Nandika Sarasi Raychaudhari,Sohalia Vijaykumar,Tridhara Rathiea,Shaila Sundha Soma,Natun Yogish,Shantala Kapoor,Chitkala Satin,Sharika Pal,Hamsa Ramadhin,Anika Kalpna,Neepa Naini,Preyasi Sucharita Tyagri,Chandanika Prayag,Yasmin Chitnis,Visala Nikesh,Shabnum Joshita Amra,Jowaki Bahula Nitesha,Jamuna Gavaskar,Sweta Fair Complexioned Saini,Dhanyata Raza,Kamala Somendra,Riti Lavali Ravipati,Mukula Saikumar,Ratnalekha Surendar,Iravati Mangeshkar,Shreeparna Khodaiji,Smaram Uttara,Jayalalita Sajja,Zeenat Nuregesan,Omkar Mirajkar,Shevantilal Jagarlamudi,Dev Mahale,Mitesh Sara,Sanjog Nidheesh,Satyanarayan Simha,Yudhajit Virinchi,Mehdi Swanimathan,Wali Ruma,Subhadra Hariom Rupali,Pran Yavatkar,Iman Subudhi,Nitin Shriharsha,Sanjay Sukhjinder,Dharma Solkar,Pratik Khanderia,Nairit Margasahayam,Chandrakishore Kota,Abhivira Raghavendra,Ajendra Gajraj,Shubhankar Kaul,Shashishekhar Shaje,Sumit Sivaram,Achyuta Kulkarni,Samarendu Innuganti,Chetana Sowrirajan,Paritosh Saligrama,Shameek Pahwa,Hanumant Sharmistha,Deveshwar Thundyil,Sankarshan Saquib Venkataramanan,Sitikantha Phutika,Paramananda Prabuddha Munusamy,Neelkanta Sreedevan,Suvimal Chitrangda,Shyam Tantry,Hans Udaya,Lagan Suman,Senajit Apte,Fanishwar Sadalge,Amitiyoti Sibabrata,Gangeya Maitreya,Anil Sury,Dhananjay Prasana,Sudhakar Chinnappan,Ninad Pancholi,Chandraraj Preetinder,Tarachand Guntur,Vighnesh Vignesh Vinuta,Ravinandan Kanwar,Bhagirath Gilab,Yagna Boparai,Nimai Sadaram,Ujwal Sunny,Mitra Channarayapatra,Abhimanyusuta Sukanya,Timin Yadavalli,Manasi Vamsi,Sujay Makam,Archan Nilofer,Kamadev Joshipura,Sayed Srivaths,Yaduvir Meherhomji,Darshan Somasundara,Ratish Vasumati,Saket Konkipudi,Yashpal Sagoo,Barindra Nagedwaran,Soumyakanti Rathore,Srijan Kalpak,Gurdeep Vallurupa,Tulsidas Madan,Rushil Sharad Pichai,Ashu Abhijvala Vamshi,Nikhil Saumya,Aslesh Rajani,Kamran Omarjeet,Ambarish Vijaysaradhi,Chirayu Mista,Satyendra Varsha,Ojas Nishar,Jehangir Rajasimha,Hasit Sudhansu,Sumant Manikkalingam,Ram Vivek Nivedita,Ambuj Kandula,Abhra Soumen,Akshat Kirit Jeeri,Mani Baboor,Avatar Ghemawat,Nishanath Kuberchand Ramnarine,Qutub Tarpa,Yuvraj Vijanyendra Venkatesan,Mohita Ujagar Udutha,Jnyaneshwar Prateek Rajah,Mainak Yavatkar,Uddhar Vaibhav,Amit Somu,Nripa Manchapora,Syamantak Nira,Mrinal Jagder,Sakina Patankar,Deeptimoyee Shamir,Dhara Samderiya,Champabati Somasundara,Sharmistha Somalakshmi Mandava,Ila Ramakan,Shreyashi Makarand,Angana Vakil,Samata Archisha Labhsha,Ulka Manjusha,Sunandita Lalitha,Narayani Thiruvengadathan,Swasti Shivani,Ina Meenakshisundaram,Yashawini Subas,Nirmayi Virasana,Ishita Ganapathiraman,Padmavati Sundha Rai,Swapna Bhagwanti Senapathy,Keshini Vasava,Magana Rukma Shvetank,Shree Ganapathy,Sadhvi Mangesh,Deepa Shiladitya,Suranjana Pals,Pooja Sreedhar,Sikta Barendran,Aaarti Nandakishore,Malaya Nagi,Nishithini Kambhampati,Tambura Anwesha Nishar,Suvarnarekha Sachin,Urshita Dhurvasula,Prita Pandian,Tanmaya Reema,Hasumati Phutika,Vaishavi Patanjali,Timila Choudhary,Trinetra Yerramilli,Kajjali Jayasinghe,Sona Vittal,Prerana Narmada,Kanaklata Chaudhry,Sultana Yesh,Shinjini Kutumbaka,Padma Huggahilli,Chanchala Rangaswamy,Chandrabhaga Kampan,Ushakiran Shaila Trupti,Rituraj Rebani,Shashank Shantinath Vijaya,Wajidali Maninder,Divakar Suryadevara,Vikramaditya Mukul,Farokh Shriharsha,Shvetang Gorti,Umanant Uppalapati,Parasmani Manchapora,Manu Suvrata Mousumi,Ekram Srivas,Aadesh Macharla,Aalap Arindam Shrirang,Indulal Srivaths,Bhupendra Akram Tanuj,Kaushik Vaish,Sarasija Manasi,Sunil Mahatapa,Satyaprakash Chaudhari,Ruchir Charan,Madhav Tarit,Aravali Rishabh Sitha,Gurcharan Lalitesh,Asgar Mallick,Uday Ramaswamy,Kamalapati Shrikant,Nripendra Vani,Uttam Naeem,Kanha Anuj Ruchi,Madhusudan Mayekar,Nanak Pradyot Srinath,Deepak Ranajay Kelaka,Deenabandhu Malti,Phalguni Keskar,Tarik Khanderia,Amal Subramaniam,Bibhas Omkar,Ajendra Mehendale,Gaganvihari Upender,Anbarasu Shubhabrata,Omar Maneesh,Payas Suryanarayanan,Jyotiranjan Konduru,Amogh Sunondo,Ramkishore Tejeshwar Mandar,Gursharan Virinchi,Nigam Koushika,Ihit Neel,Chitrarath Prasham Yeluri,Martand Imani,Riyaz Duvvoori,Mayank Vaidya,Chakor Vajpeyi,Arnav Anup Perumal,Digamber Nailadi,Venimadhav Tasha,Akul Jandhyala,Ekanga Pewar,Patralika Pasram,Ushakanta Sankaranarayanan,Achalapati Subramanya,Anoop Gade,Valmik Sachin,Amitrasudan Nitesha,Saquib Manasa,Abhirup Keshavan,Amish Satyanarayan Swarnkar,Akmal Shorey,Pulish Sujan,Loknath Gala,Vilas Dhaliwal,Achanda Primal,Nadir Prasun Srivastava,Shesh Punj,Kamalakar Vinata,Vishnu Ramjee,Kanwaljeet Satvamohan Choudhary,Nityanand Ghandi,Sudhi Dharuna,Prabuddha Sinduvalli,Ajamil Charu,Vipra Michandani,Indukanta Brahmabrata Sattar,Mandhatri Mukunda,Lochan Yogish,Hemaraj Melliyal,Chandrakumar Arumugan Sourav,Navnit Modi,Samarjit Neelakantachar,Raghunandan Agnikumara Parameswaran,Asgar Radhabinod,Harshvardhan Phadnis,Paresh Tandekar,Shattesh Trishwant,Adalarasu Bilva Maran,Khushal Chethan,Jaidev Udutha,Lalitkishore Pujar,Pradeep Seshan,Suyash Navarathna,Bhooshan Naeem,Chitraksh Lalima,Tejomay Satyavrat,Soumil Punnoose,Prajin Mahanthapa,Vandan Muppala,Tukaram Abhinava Vonguru,Mrigankamouli Swati,Vallabh Nageswar,Sumanta Udit,Pradosh Swagat,Satyapriya Santharam,Manavendra Pusan,Devang Sury,Pinak Kishore,Meghnad Mitali,Harmendra Vasava,Sudeep Dinkerrai,Alhad Pai,Naval Rupesh,Ramkumar Nizami,Abhidi Varun,Pavak Sujeev,Manoj Pasram,Yashas Rana,Vanajit Kandathil,Lalitaditya Purushottam Multani,Pururava Badesha,Tarachand Jeoomal,Rajyeshwar Mansukh Jagder,Arumugan Prabhat,Teerth Sundhararajan,Nalin Ramasubramanian,Lambodar Neeru,Kedarnath Sudha,Nirvan Prafulla Khodabhai,Pratosh Senajit Uppuluri,Nityagopal Angada Chaudhry,Sushruta Anuha Vanchinathan,Nishit Chakrabarti,Niranjan Nita,Uttam Toodi,Sheetal Rangwala,Yugandhar Deol,Rohitasva Sarasvan,Waman Suryanarayanan,Acharyanandana Varki,Rakesh Chandar,Viplab Rekha,Ritvik Mittur,Sudipti Naidoo,Dhatri Tapan,Parameshwari Sudhanshu,Lalitha Maddukuri,Anala Chheda,Yashoda Kanive,Sulabha Ramila,Tambura Pennathur,Shobha Valli,Muniya Naagesh,Sharvani Swagato,Amodini Hemalatha,Binodini Vamshi,Ishana Ahalya Pendharkar,Tamasi Meenakshisundaram,Roma Lolaksi,Anika Mandar,Sarita Pramila,Laabha Vikul,Chandrika Gundugollu,Aboil Rupali,Ratnajyouti Tikekar,Siddhima Chandrashaker,Ekavali Ulind,Omana Chandrashekar,Bhamini Sharadini Lalitesh,Taruni Nerurkar,Foolan Urimindi,Ahalya Kathrada,Waheeda Pavani,Maithili Sameer,Nityapriya Sarmad,Kshanika Rajhans Swathi,Harinakshi Akriti Shamir,Diksha Yudhajit,Anurati Viraj,Bimala Rangaswamy,Vidhut Gajraj,Chandika Sawardekar,Devangana Pankajakshan,Aparijita Monica,Mohana Chetlapalli,Himagouri Chandrakala,Soumya Subbarat,Tehzeeb Shomik,Sudevi Vemireddy,Maanika Rangarajan,Vidula Amala Perumal,Bharati Taksa,Pritika Subbarayan,Surotama Swetha,Laasya Narmada,Lalitha Manchapora,Niranjana Chandran,Manisha Rachna,Lajwati Podury,Diksha Gorawala,Renuka Shriharsha,Vrunda Sahar,Chinmayi Ranjita Subbanna,Ujjanini Rajabhushan,Madirakshi Swathi,Juily Pasapuleti,Sarasvati Varuni,Shilavati(a River Pushkarini,Shagufta Mathrubootham,Shobhita Keerthana,Manana Naagesh,Surupa Kirmani,Atasi Markandeya,Deeba Sumita Sunther,Agrima Kothandaraman,Taru Chandrashaker,Bandana Sheba,Trishna Shashwat,Aradhana Chandramouleeswaran,Sampada Sarangapani,Hasita Mahale,Adrika Sarangarajan,Bala Smirti,Surama Vandana,Tarakeshwari Ramamoorthy,Rohini Bhanghoo,Zarine Vipin,Rasika Saraf,Jyotika Makam,Shambhavi Nadkarni,Dristi Srikumar,Chanchala Sujeev,Panchali Pasram,Shampa Surabhi,Ulupi Verma,Atreyi Murli,Vanani Manjari,Devi Rekha Manju,Hemlata Jayasinghe,Amrapali Prerana,Ila Unnikrishnan,Deepanwita Rajashi,Malavika Smrita Dasgupta,Gajagamini Harathi Sankaranarayanan,Madhulata Manavi,Pratigya Reba,Meghamala Swarnkar,Kriti Saeed,Lajwati Shail,Chaitaly Prasannakumar,Dhanishta Uma Pundarik,Nipa Sugriva,Lalitamohana Prateek,Somalakshmi Shinu,Kripa Ramamohan,Ambuja Sahgal,Siddhima Rangnekar,Sananda Sadhan Konduru,Shradhdha Ubriani,Lalana Vidyashankar,Shubhada Sejal Vishnavi,Maithili Pal,Hema Shujauddin,Chhaya Koganti,Suvarnarekha Nagabhushana,Ratnali Kalluri,Shilpita Sompalli,Binata Bulbul Surnilla,Tarini Rajarshi,Himani Tyagi,Yasmine Mageshkumar,Gitanjali Sophia,Alaknanda Mallya,Durga Chiba,Chaitan Sreekanthan,Satyavati Shobhana Sagdo,Soma Kandula,Keshi Shadilya,Deepti Pamela,Bratati Mahadevan,Chandrabali Chandrashaker,Manjushri Sugouri Dhupam,Ekavali Dharitri Dravid,Anusha Gundamaraju,Radha Yalamanchi,Namrata Sajan,Chandika Suryanarayanan,Sharmistha Tarulata Punitha,Tejaswini Vanita Prudvi,Shrikumari Shally,Bhairavi Tarpa,Sunita Thangaraj,Akshita Raviprabha Dama,Palash Atmajyoti Yamura,Deependu Muthukrishnan,Kanad Chandran,Gopesh Kodandarami,Yasir Vishva Vineet,Dheeman Omesh,Aabheer Roopak,Balakrishna Sapra,Abhyudaya Pavithran,Omja Shashwat,Kathir Kadir Sreedharan,Shahid Pattabhiraman,Kamod Kambod Kambodi Ramprasad,Sapan Rishmal,Prabhas Adani,Pushkar Prasham Trupti,Balagovind Dasgupta,Vishal Prasana,Pratul Raghunandan,Shirish Shivakumar,Payod Chandrakala,Vineet Kota,Gaurang Nagarjuna,Hemendra Resham,Shattesh Nachik,Muralidhar Irfan Chowdhury,Devesh Kutty,Vidyacharan Puja,Talat Senapathy,Kamadev Suriyaprakash,Samudrasen Tagore,Ushakanta Rajashi,Ranganath Parmar,Viswanath Yavar,Suvimal Shantinath,Trivikram Sudershan,Riyaz Mandyam,Paritosh Mista,Nishikanta Sunanda,Nirupam Ravipati,Parantapa Padmakant,Jihan Sandipa,Jnyaneshwar Umakant Thogulva,Balvindra Satyakam Sukumar,Amalesh Monica,Rahul Dama,Geet Pramath,Gudakesha Chapal,Satyanarayan Suman,Bhanu Cherupara,Akshat Kalpak,Aloke Nandy,Lalit Battacharjee,Lalitaditya Trilochana,Ekram Varindra Jai,Dhritiman Gutta,Jignesh Sangha,Suresh Pyaremohan Sujan,Raghu Rituraj Srijoy,Kanwaljeet Sekhar,Sukrit Muthukrishn,Basanta Pullela,Parkash Vijaykrishna,Fanish Davuluri,Pushkar Sabeena,Harihar Kumar,Ramkishore Nissim Shahid,Asija Kumbla,Dulal Balvindra Lanka,Paranjay Shubhendu,Nandan Suprakash Potla,Lalitchandra Sarfaraz Yudhajit,Sanjivan Sumedh,Sugriva Nirguna,Kanad Sukanya,Sadanand Pasuma,Ronak Sudhansu,Atul Atulya Lakhani,Ritvik Mittal,Abhijat Majety,Chittaranjan Maitryi,Raja Nagaraj,Mahin Rabindra,Abhayaprada Ulind,Atmajyoti Sangawar,Jaysukh Vineeta,Ratnakar Joardar,Vajrapani Soni,Abhyudita Maitreya,Rohan Chittibabu,Pathin Hindocha,Nabarun Sanjog Suranjan,Acaryatanaya Shashikanth,Jagannath Sen,Shatrunjay Jannavi,Prabuddha Sarwate,Bhajan Kalluri,Lakshman Sony,Agniprava Variya,Keshav Thamry,Hemangini Tanmaya,Shashibala Vinita,Varana Vedi Sardesai,Resham Rengarajan,Kalavati Nilufar,Anala Hemalatha,Mahasweta Chinnakannan,Abani Rukma Swami,Saryu Sathaye,Kumkum Palia,Tusharkana Sidda,Madhulika Shafiqul,Mala Kapoor,Lipika Suravinda,Abha Ulla,Shrigeeta Ganapathiraman,Urmil Mrinal Nailadi,Amoda Shinjinee,Tapasi Kurian,Indrani Kallichuran,Fatima Uttanka,Ratnaprabha Vadlamani,Nauka Shachi,Ira Pillay,Hiral Venugopalan,Waheeda Kamalakshi Rekha,Ila Venkatasubramani,Radhika Atasi Nandedkar,Mahati Ilango,Tripta Prithviraj,Satyarupa Bhoomi Surnilla,Anjushri Pashupathy,Kanakpriya Thuvaradran,Ishani Rathore,Dhanishta Prithvi,Madhavilata Naiya Muthukrishn,Sneha Shomik,Pragyaparamita Sahgal,Rukma Jeoomal,Divya Priti,Pritika Pramath,Vanhi Labhsha,Madhubala Radheshyam,Kranti Shaban,Jeevana Parmar,Samata Shamir,Parnashri Kulkarni,Kishori Kurapati,Sonika Chetlapalli,Vanita Gambhir";

        Map<Integer,String> orgMap = new LinkedHashMap<>();
        Map<Integer,String> custMap = new LinkedHashMap<>();
        Map<Integer,String> vendorMap = new LinkedHashMap<>();
        Map<Integer,String> userMap = new LinkedHashMap<>();

        String[] orgArr = orgs.split(",");
        String[] customerArr = customers.split(",");
        String[] vendorArr = vendors.split(",");
        String[] userArr = users.split(",");
        int i=1;
        int j=1;
        for(String s:orgArr){
            //System.out.println(i+";"+s+";"+s);
            String[] name = s.split(" ");
            orgMap.put(i,"@"+name[0].toLowerCase().trim()+".com");
            i++;
        }
        i=1;
        j=1;
        for(String s:customerArr){
            //System.out.println(i+";"+s+";"+s+";"+j);
            String[] name = s.split(" ");
            custMap.put(i,"@"+name[0].toLowerCase().trim()+".com");
            if(i%10==0){
                j++;
            }
            i++;
        }
        i=1;
        j=1;
        for(String s:vendorArr){
            //System.out.println(i+";"+s+";"+s);
            String[] name = s.split(" ");
            vendorMap.put(i,"@"+name[0].toLowerCase().trim()+".com");
            i++;
        }
        i=1;
        j=1;
        Map<String,String> uniqueNames = new HashMap<>();
        for(String s:userArr){
            String[] names = s.split(" ");
            //System.out.println(i+";"+names[0].toLowerCase()+";"+names[0]+";"+names[1]);
            uniqueNames.put(names[0].toLowerCase(),s);
            i++;
        }
        String userHeader = "id;first_name;last_name;email;password;login;organisation_id;customer_id;vendor_id;authority_id;keycloak_id";
        i=1;
        j=1;
        int k=1;
        int l=1;
        int m=1;
        int n=1;
        for(Map.Entry<String,String> entry: uniqueNames.entrySet()){
            String[] names = entry.getValue().split(" ");
            if(i<=20) {
                //System.out.println(l + ";" + names[0] + ";" + names[1] + ";"+names[0].toLowerCase()+orgMap.get(i)+";12345678;"+names[0].toLowerCase()+";"+i+";;;2;");
                i++;
            }
            if (i == 21 && j <= 200 && l>20) {
                //System.out.println(l + ";" + names[0] + ";" + names[1] + ";" + names[0].toLowerCase() + custMap.get(j)+";12345678;"+names[0].toLowerCase()+";;"+j+";;3;");
                   j++;
            }
            if (i == 21 && j == 201 && l>220 && n<=50){

                //System.out.println(l + ";" + names[0] + ";" + names[1] + ";"+names[0].toLowerCase()+vendorMap.get(n)+";12345678;"+names[0].toLowerCase()+";;;"+n+";4;");
                n++;
            }
            if (i == 21 && j == 201 && l>270 && n==51){
                if(k%10==0){
                    if(m<50)
                        m++;
                }
                //System.out.println(l + ";" + names[0] + ";" + names[1] + ";"+names[0].toLowerCase()+vendorMap.get(m)+";12345678;"+names[0].toLowerCase()+";;;"+m+";5;");
                k++;
            }
            l++;
        }
        i=1;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;organisation_id");
        for(;i<21;i++){
            //System.out.println(i+";"+i+";"+i);
        }
        i=1;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;authority_id");
        for(;i<21;i++){
            //System.out.println(i+";"+i+";2");
        }
        i=21;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;authority_id");
        for(;i<221;i++){
            //System.out.println(i+";"+i+";3");
        }
        i=1;
        j=21;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;customer_id");
        for(;i<221;i++){
            //System.out.println(i+";"+j+";"+i);
            j++;
        }
        i=221;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;authority_id");
        for(;i<271;i++){
            //System.out.println(i+";"+i+";4");
        }
        i=221;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;vendor_id");
        for(;i<271;i++){
            //System.out.println(j+";"+i+";"+j);
            j++;
        }

        i=271;
        j=1;k=1;l=1;m=1;n=1;
        //System.out.println("id;user_id;authority_id");
        for(;i<uniqueNames.size();i++){
            //System.out.println(i+";"+i+";5");
        }
        i=221;
        j=1;k=1;l=51;m=1;n=1;
        System.out.println("id;user_id;vendor_id");
        for(;i<uniqueNames.size();i++){
            if(j%10==0){
                if(k<50)
                    k++;
            }
            System.out.println(l+";"+i+";"+k);
            j++;
            l++;
        }
    }
}
