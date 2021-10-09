package web

import (
	"encoding/json"
	"log"
	"net/http"

	"github.com/muktiarafi/shop/shop-common/exception"
)

type Response struct {
	Status  int         `json:"status"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

func NewResponse(status int, message string, data interface{}) *Response {
	return &Response{
		Status:  status,
		Message: message,
		Data:    data,
	}
}

func (r *Response) SendJSON(w http.ResponseWriter) error {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(r.Status)

	return json.NewEncoder(w).Encode(r)
}

func SendError(w http.ResponseWriter, err error) {
	log.Println(err)

	var errorResponse *Response
	if _, ok := err.(*exception.Ex); ok {
		code := exception.ExceptionCode(err)
		errorResponse = &Response{
			Status:  exception.ExceptionCodeToHTTPStatusCode(code),
			Message: code,
			Data:    exception.ExceptionMessage(err),
		}
	} else {
		errorResponse = &Response{
			Status:  http.StatusInternalServerError,
			Message: exception.EINTERNAL,
			Data:    []string{"Server Error, Try Again later."},
		}
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(errorResponse.Status)

	json.NewEncoder(w).Encode(errorResponse)
}
