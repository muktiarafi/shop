package exception

import (
	"bytes"
	"fmt"
	"net/http"
	"strings"

	"github.com/pkg/errors"
)

const (
	EUNAVAILABLE  = "Service Unavailable"
	EINTERNAL     = "Internal Error"
	EUNAUTHORIZED = "Not Authorized"
	ECONFLICT     = "Conflict"
	EINVALID      = "Invalid"
	ENOTFOUND     = "Not Found"
	EFORBIDDEN    = "Forbidden"
)

type Ex struct {
	Code    string
	Message []string
	Op      string
	Err     error
}

func (e *Ex) Error() string {
	var buf bytes.Buffer

	if e.Op != "" {
		fmt.Fprintf(&buf, "%s: ", e.Op)
	}

	if e.Err != nil {
		buf.WriteString(fmt.Sprintf("%+v\n", e.Err))
	} else {
		if e.Code != "" {
			fmt.Fprintf(&buf, "<%s> ", e.Code)
		}
		var s strings.Builder
		for _, v := range e.Message {
			s.WriteString(fmt.Sprintf("%s. ", v))
		}
		buf.WriteString(s.String())
	}
	return buf.String()
}

func ExceptionMessage(err error) []string {
	if err == nil {
		return []string{""}
	} else if e, ok := err.(*Ex); ok && e.Message != nil {
		return e.Message
	} else if ok && e.Err != nil {
		return ExceptionMessage(e.Err)
	}

	return []string{"Server Error, Try Again later."}
}

func ExceptionCode(err error) string {
	if err == nil {
		return ""
	} else if e, ok := err.(*Ex); ok && e.Code != "" {
		return e.Code
	} else if ok && e.Err != nil {
		return ExceptionCode(e.Err)
	}
	return EINTERNAL
}

func ExceptionCodeToHTTPStatusCode(code string) (statusCode int) {
	switch code {
	case EUNAUTHORIZED:
		statusCode = http.StatusUnauthorized
	case EINVALID:
		statusCode = http.StatusBadRequest
	case ENOTFOUND:
		statusCode = http.StatusNotFound
	case ECONFLICT:
		statusCode = http.StatusConflict
	case EUNAVAILABLE:
		statusCode = http.StatusServiceUnavailable
	case EFORBIDDEN:
		statusCode = http.StatusForbidden
	default:
		statusCode = http.StatusInternalServerError
	}

	return
}

func NewSingleMessageException(code, message string, err error) *Ex {
	return &Ex{
		Code:    code,
		Message: []string{message},
		Err:     err,
	}
}

func NewValidationException(message []string) *Ex {
	return &Ex{
		Code:    EINVALID,
		Message: message,
		Err:     errors.New("validation error"),
	}
}
