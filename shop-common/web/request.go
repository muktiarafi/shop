package web

import (
	"encoding/json"
	"io"

	"github.com/muktiarafi/shop/shop-common/exception"
)

func Bind(r io.Reader, v interface{}) error {
	err := json.NewDecoder(r).Decode(v)
	if err != nil {
		exc := exception.NewSingleMessageException(
			exception.EINVALID,
			"Invalid Payload",
			err,
		)
		return exc
	}
	return nil
}
