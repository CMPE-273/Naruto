package main

import (
	"encoding/json"
	"log"
	"net/http"
	"github.com/drone/routes"
	_ "github.com/go-sql-driver/mysql"
    "database/sql"
    _ "fmt"
)

type StudentProfile struct{
	SjsuID 			string 		`json: "sjsu_id"`
	InstanceID 		string 		`json: "instance_id"`
} 



func checkErr(err error) {
    if err != nil {
        panic(err)
    }
}


func main() {
	mux := routes.New()

	//	mux.Post("/key", PostValue)
	mux.Put("/:key/:value", PutValue)
	//mux.Get("/:key", GetValue)
	mux.Get("/",GetValues)

	http.Handle("/", mux)
	log.Println("Listening...")
	http.ListenAndServe(":3000", nil)
	
}

func GetValues(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe273?charset=utf8")
    checkErr(err)

    var Studentds []StudentProfile
	// query
    rows, err := db.Query("select * from app")
    checkErr(err)

for rows.Next() {
        var sjsuid string
        var instanceid string
        err = rows.Scan(&sjsuid, &instanceid)
        checkErr(err)
        student := StudentProfile{sjsuid,instanceid}
        Studentds = append(Studentds,student)
    }

	db.Close()

    if err := json.NewEncoder(w).Encode(Studentds); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)

}

func PutValue(w http.ResponseWriter, r *http.Request){

		params := r.URL.Query()
		key := params.Get(":key")
		value := params.Get(":value")

		//fmt.Println("storing key " + key+"\n token "+ value )

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe273?charset=utf8")
    checkErr(err)

    // update
    stmt, err := db.Prepare("update app set instance_id=? where sjsu_id =? ")
    checkErr(err)

    _, err = stmt.Exec(value, key)
    checkErr(err)

    db.Close()

	//send ok if success
    w.WriteHeader(http.StatusNoContent)

	}